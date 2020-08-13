package com.exam.bank.service

import com.exam.bank.dto.AuthIcFirstIn
import com.exam.bank.dto.AuthIcOut
import com.exam.bank.dto.AuthIcSecondIn
import com.exam.bank.dto.ReqExtBankIn
import com.exam.bank.repo.mybatis.AcctMapper
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.core.error.BizException
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 계좌 서비스
 */
@Service
class AcctService : BaseService() {

    @Autowired lateinit var mapperAcct: AcctMapper   // 계좌 매퍼
    @Autowired lateinit var serviceUser: UserService // 사용자 서비스

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

        // init
        val userId = area.commons.user!!.userId

        // validation: 기존 인증 단계 조회
        val stgCd = mapperAcct.selectOneAcctStg(userId)
        if (stgCd != null && stgCd >= "B0")
            throw BizException("신분증 인증을 기 완료한 고객 입니다.")

        // 인증단계 저장 [A0: 신분증 제출 필요 및 진행중]
        if (stgCd == null)
            mapperAcct.insertAcctStg(userId, "A0")

        // 인증 AP 호출
        val res = khttp.post(
                url = "$apUrl/auth/ic",
                json = mapOf("image" to input.image)
        )
        log.debug("ap status=${res.statusCode} result=${res.jsonObject}")

        if (res.statusCode != 200 || res.jsonObject["result"] != "OK") {
            throw BizException("재촬영 시도 해 주세요.")
        }

        // 인증단계 저장 [A1: 신분증 제출 및 인증 완료]
        mapperAcct.updateAcctStg(userId, "A1")

        return Gson().fromJson(res.jsonObject.toString(), AuthIcOut::class.java)

    }

    /**
     * 신분증 인증 2단계( 교정정보 제출 )
     * - 고객 AP 호출: 고객정보 Update
     */
    fun authIcSecond(input: AuthIcSecondIn) {

        // init
        val userId = area.commons.user!!.userId

        // validation
        val stgCd = mapperAcct.selectOneAcctStg(userId)
        if (stgCd == null || stgCd < "A1")
            throw BizException("신분증 촬영이 이루어지지 않은 고객 입니다. 신분증 촬영부터 다시 시도해 주세요. $stgCd")

        if (stgCd >= "B0")
            throw BizException("신분증 인증을 기 완료한 고객 입니다.")

        // 고객 AP 호출: 고객정보 Update
        val res = khttp.post(
                url = "$apUrl/cln/auth-info",
                json = mapOf("name" to input.name, "ci" to input.ci)
        )
        log.debug("ap status=${res.statusCode}")

        if (res.statusCode != 200) {
            throw BizException("고객 정보 저장 중 에러 발생. 재시도 해주세요.")
        }

        // 인증단계 저장[B0: 이체 계좌입력 필요]
        mapperAcct.updateAcctStg(userId, "B0")

    }


    /**
     * 이체 등록 요청
     */
    fun reqExtBank(input: ReqExtBankIn) {

        // init
        val userId = area.commons.user!!.userId

        // validation
        val stgCd = mapperAcct.selectOneAcctStg(userId)

        if (stgCd == null || stgCd < "B0")
            throw BizException("신분증 촬영 부터 진행 해 주세요.")

        // 사용자명 조회
        val user = serviceUser.getUserById(userId)
        if (user == null)
            throw BizException("당행 고객 정보를 찾지 못하였습니다. 고객센터에 문의 해 주세요.")

        // 타행 계좌 명의 조회
        khttp.async.get(
                url = "$apUrl/ext/acct",
                params = mapOf("acctNo" to input.acctNo, "bankCd" to input.bankCd),
                timeout = 1.0,
                onError = {
                    log.info("onError: $message")
                    when (message) {
                        "Read timed out" -> {


                        }
                        else -> {
                            throw BizException("타행 계좌의 명의 정보를 얻어오는데 실패 하였습니다. 재시도 해주세요.")
                        }
                    }
                }) {
            log.info("success: $statusCode, $text")
        }

    }


}

