package com.exam.bank.service

import com.exam.bank.dto.*
import com.exam.bank.repo.jpa.AcctRepo
import com.exam.bank.repo.mybatis.AcctMapper
import com.exam.bank.service.ExtService.TR_ID
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.core.error.BizException
import com.exam.fwk.custom.util.StringUtils
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 계좌 서비스
 */
@Service
class AcctService : BaseService() {

    @Autowired lateinit var mapperAcct: AcctMapper   // 계좌 매퍼 (MyBatis)
    @Autowired lateinit var repoAcct: AcctRepo       // 계좌 레포 (JPA)
    @Autowired lateinit var serviceUser: UserService // 사용자 서비스
    @Autowired lateinit var serviceExt: ExtService   // 대외거래 서비스
    @Autowired lateinit var servicePush: PushService // PUSH 서비스

    /**
     * 계좌 인증 단계 코드 조회
     * - 해당 사용자의 단계가 없다면 A0 리턴
     */
    fun getAcctStgCd(userId: Int) = mapperAcct.selectOneAcctStg(userId) ?: "A0"

    /**
     * 신분증 인증 1단계( 이미지 제출 )
     * - 인증 AP 호출: 신분증 이미지 분석 요청
     * - 인증단계 저장
     */
    fun authIcFirst(input: AuthIcFirstIn): AuthIcOut {

        // Init --------------------------------------------------------------------------------------------------------
        val userId = area.commons.user!!.userId

        // validation: 기존 인증 단계 조회
        val stgCd = mapperAcct.selectOneAcctStg(userId)
        if (stgCd != null && stgCd >= "B0")
            throw BizException("신분증 인증을 기 완료한 고객 입니다.")

        // Main --------------------------------------------------------------------------------------------------------
        // 인증단계 저장 [A0: 신분증 제출 필요 및 진행중]
        if (stgCd == null)
            mapperAcct.insertAcctStg(userId, "A0")

        // 인증 AP 호출
        val res = khttp.post(
                url = "$apUrl/auth/ic",
                json = mapOf("image" to input.image)
        )

        if (res.statusCode != 200 || res.jsonObject["result"] != "OK") {
            throw BizException("재촬영 시도 해 주세요.")
        }

        // End ---------------------------------------------------------------------------------------------------------
        updateAcctStgByFlow(userId, "A1") // 인증단계 저장 [A1: 신분증 제출 및 인증 완료]

        return Gson().fromJson(res.jsonObject.toString(), AuthIcOut::class.java)

    }

    /**
     * 신분증 인증 2단계( 교정정보 제출 )
     * - 고객 AP 호출: 고객정보 Update
     */
    fun authIcSecond(input: AuthIcSecondIn) {

        // Init --------------------------------------------------------------------------------------------------------
        val userId = area.commons.user!!.userId

        // validation
        val stgCd = mapperAcct.selectOneAcctStg(userId)
        if (stgCd == null || stgCd < "A1")
            throw BizException("신분증 촬영이 이루어지지 않은 고객 입니다. 신분증 촬영부터 다시 시도해 주세요. $stgCd")

        if (stgCd >= "B0")
            throw BizException("신분증 인증을 기 완료한 고객 입니다.")

        // Main --------------------------------------------------------------------------------------------------------
        // 고객 AP 호출: 고객정보 Update
        val res = khttp.post(
                url = "$apUrl/cln/auth-info",
                json = mapOf("name" to input.name, "ci" to input.ci)
        )

        if (res.statusCode != 200) {
            throw BizException("고객 정보 저장 중 에러 발생. 재시도 해주세요.")
        }

        // End ---------------------------------------------------------------------------------------------------------
        updateAcctStgByFlow(userId, "B0") // 인증단계 저장[B0: 이체 계좌입력 필요]

    }


    /**
     * 타행계좌 인증 처리
     */
    fun procExtAcctAuth(input: T1Body): ReqExtBankOut {

        // Init --------------------------------------------------------------------------------------------------------
        val userId = area.commons.user!!.userId
        val stgCd = mapperAcct.selectOneAcctStg(userId)      // 현재 인증 단계 조회

        // validation
        if (stgCd == null || stgCd < "B0")                   // IF 해당 사용자 인증 단계가 B0[계좌 입력 필요, 신분증OK] 이전 이라면
            throw BizException("신분증 촬영 부터 진행 해 주세요.")

        if (stgCd >= "C0")                                   // IF 해당 사용자 인증 단계가 B2[이체 요청] 이상 이라면
            throw BizException("[고객 센터로 문의하세요] [발생소지 없음, 인증 완료 사용자] 모바일에서 콘트롤.")

        if (stgCd >= "B2")                                   // IF 해당 사용자 인증 단계가 B2[이체 요청] 이상 이라면
            throw BizException("기 계좌 이체 요청 중입니다.")

        // 사용자명 조회
        val user = serviceUser.getUserById(userId)
        if (user == null)
            throw BizException("당행 고객 정보를 찾지 못하였습니다. 고객센터에 문의 해 주세요.")

        // Main --------------------------------------------------------------------------------------------------------
        input.userId = userId
        input.userNm = user.userNm
        reqExtAcct(input)           // 타행 계좌 명의 조회

        // End ---------------------------------------------------------------------------------------------------------
        return ReqExtBankOut(result = "OK")
    }

    /**
     * 타행 계좌 명의 조회
     */
    fun reqExtAcct(input: T1Body) {

        // 사용자 조회
        val user = serviceUser.getUserById(input.userId)
        if (user == null)
            throw BizException("존재하지 않는 사용자 입니다.")

        // T1 전문 요청
        val body = Gson().toJson(input)
        serviceExt.regExtTr(TR_ID.T1, input.userId, body)

    }

    /**
     * T1 전문 (타행 계좌 조회) 수신 시, 처리
     */
    fun recvT1(resNm: String, userId: Int, bankCd: String, acctNo: String): Boolean {

        // Init --------------------------------------------------------------------------------------------------------
        val user = serviceUser.getUserById(userId) // 사용자 조회
        if (user == null) {
            log.error("[B1-B2] 계좌 명의 조회 중 에러 발생.")
            return false
        }

        // validation
        if (resNm != user.userNm) {                             // IF 명의가 일치하지 않는다면
            servicePush.regPush(PushService.PUSH_ID.P3, userId) //   P3: 명의가 일치하지 않습니다. 다른 계좌로 진행 해 주세요.
            updateAcctStgByFlow(userId, "B0")                   //   인증단계 저장[B0: 이체 계좌입력 필요]
            return false
        }

        // Main --------------------------------------------------------------------------------------------------------
        serviceExt.cancelEtr(TR_ID.T1, userId)                          // T1 전문 모두 취소
        servicePush.regPush(PushService.PUSH_ID.P2, userId)             // 사용자 푸쉬: P2: 타행 계좌 명의 확인 완료. 다음단계 진행.

        // T2 전문 등록
        val input = T2Body(bankCd, acctNo, createWord(userId), userId)  // 해당 사용자의 인증단어 생성
        val body = Gson().toJson(input)
        serviceExt.regExtTr(TR_ID.T2, userId, body)                     // T2 전문 요청 등록, 이후 ExtTaskService 에서 전문 처리
        updateAcctStgByFlow(userId, "B2")                               // 인증단계 저장[B2: 이체 대기 중]

        // End ---------------------------------------------------------------------------------------------------------
        return true
    }

    /**
     * 인증단어 생성
     */
    fun createWord(userId: Int): String = StringUtils.getRandomString(4)

    /**
     * 인증 단계 수정
     * - A0: 신분증 제출 필요 및 진행중
     * - A1: 신분증 제출 및 인증 완료
     * - B0: 이체 계좌입력 필요
     * - B2: 이체 대기 중
     * - B3: 1원 이체 시작
     * - C0: 단어 인증 필요
     * - C1: 단어 매칭 OK
     * - C2: 계좌 생성 완료
     */
    fun updateAcctStgByFlow(userId: Int, inStgCd: String, isForce: Boolean = false) {

        val curStgCd = mapperAcct.selectForUpdate(userId) // 현재 인증 단계 조회

        val isValid = when {
            curStgCd == null -> true
            (curStgCd >= "B0") && (inStgCd < "B0") -> false  // 계좌 입력 단계로 넘어가면, 신분증 단계로 내려가는 것 불가
            (curStgCd == "B2") && (inStgCd < "B2") -> false  // 이체 대기 중일 땐, 이하 단계로 수정 불가
            (curStgCd == "B3") && (inStgCd <= "B3") -> false // 이체 시작 되었을 때, 다시 이체 시작이 될 수 없다.
            else -> true
        }

        if (!isValid && !isForce) {
            log.error("인증 단계 수정 시도 하였으나 논리 오류로 미 수정. 현재[$curStgCd], 변경[$inStgCd]")
            return
        }

        mapperAcct.updateAcctStg(userId, inStgCd) // db update: 인증 단계 변경
    }

    /**
     * T2 전문 (타행 계좌 조회) 수신 시, 처리
     * - 인증 단계 수정 [C0: 단어 인증 필요]
     * - 인증 단어 저장
     */
    fun recvT2(etcCtn: String, input: T2Body): Boolean {

        // 사용자 조회
        val user = serviceUser.getUserById(input.userId)
        if (user == null) {
            log.error("[B3-C0] 계좌 이체 후 처리 중 에러 발생.")
            return false
        }

        // 인증 단계 수정 [C0: 단어 인증 필요]
        updateAcctStgByFlow(input.userId, "C0")

        // 인증 단어 저장
        mapperAcct.updateEtcCtn(input.userId, etcCtn)

        // 사용자에게 푸쉬
        servicePush.regPush(PushService.PUSH_ID.P4, input.userId) // P4: 계좌 이체 적요란의 단어를 입력하시어요.

        return true

    }

    /**
     * 단어 인증
     */
    fun authWord(input: AuthWordIn) {

        // Init --------------------------------------------------------------------------------------------------------
        val userId = area.commons.user!!.userId
        val act = repoAcct.findById(userId).orElseThrow {
            throw BizException("[고객센터 문의] 인증 정보 없음")
        }

        // validation
        if(act.etcCtn != input.etcCtn)
            throw BizException("이체 내역 적요 내 기입 된 단어 4자를 올바르게 입력하세요.")

        // Main --------------------------------------------------------------------------------------------------------
        // 인증 단계 [C1: 단어 매칭 OK]
        updateAcctStgByFlow(userId, "C1")

        // 수신계좌 생성
        val res = khttp.post(
                url = "$apUrl/deps/acct",
                json = mapOf("userId" to userId)
        )

        if (res.statusCode != 200) {
            throw BizException("수신 계좌 생성 중 에러 발생. 재시도 해주세요.")
        }

        // End ---------------------------------------------------------------------------------------------------------
        // 인증 단계 [C2: 계좌 생성 완료]
        updateAcctStgByFlow(userId, "C2")

    }

    data class AuthWordIn(
            var etcCtn: String = ""
    )

}

