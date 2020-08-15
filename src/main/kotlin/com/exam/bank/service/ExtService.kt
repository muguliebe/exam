package com.exam.bank.service

import com.exam.bank.dto.T1Body
import com.exam.bank.dto.T2Body
import com.exam.bank.entity.ComEtrMst
import com.exam.bank.repo.jpa.EtrRepo
import com.exam.bank.repo.mybatis.EtrMapper
import com.exam.bank.service.PushService.PUSH_ID
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.custom.util.DateUtils
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * 뱅킹 내 대외 서비스
 */
@Service
class ExtService : BaseService() {

    @Autowired lateinit var mapper: EtrMapper        // 대외 전문 마스터 매퍼(MyBatis)
    @Autowired lateinit var repo: EtrRepo            // 대외 전문 마스터 레포(JPA)
    @Autowired lateinit var serviceAcct: AcctService // 계좌 서비스
    @Autowired lateinit var serviceExt: ExtService   // 대외거래 서비스
    @Autowired lateinit var servicePush: PushService // PUSH 서비스

    /**
     * 타행 전문 요청
     * T1: 계좌 조회
     * T2: 이체 등록 ( 1원 )
     */
    enum class TR_ID {
        T1, T2
    }

    /**
     * 전문 요청 등록
     */
    fun regExtTr(trId: TR_ID, userId: Int, body: String) {

        val inSave = ComEtrMst()
        inSave.trId = trId.name                          // 거래 ID
        inSave.trDy = DateUtils.nowDate()                // 거래 일자
        inSave.trStatCd = "01"                           // 거래상태코드 [01:요청, 02: 전송, 03:타임아웃, 04:취소, 05:수신]
        inSave.trResCd = "00"                            // 거래결과코드 [00:진행중, 01:성공, 02:실패]
        inSave.userId = userId
        inSave.createDt = DateUtils.currentTimeStamp()
        inSave.createUserId = userId
        inSave.body = body                               // 전문 아웃풋

        mapper.insertEtr(inSave)
    }

    /**
     * 전문 요청 취소
     * - 입력받은 전문 ID, 사용자 ID 와 매칭되는 모든 전문을 cancel
     * - 거래상태코드 [02:전송, 04:취소, 05:완료] 인 경우 제외
     */
    fun cancelEtr(trId: TR_ID, userId: Int) = mapper.updateListEtrCancel(trId.toString(), userId)

    /**
     * T1 전문 처리
     */
    fun requestT1(seq: Int, input: T1Body) {

        log.debug("T1 전문 처리 시작: $seq")

        // validation: 해당 사용자의 단계가 B2[이체 요청] 이상인가?
        val stgCd = serviceAcct.getAcctStgCd(input.userId)
        if (stgCd >= "B2") {
            mapper.updateOneEtrCancelBySeq(seq) // 인증단계 B2:이체대기중 으로 취소처리
            return
        }

        // 대외 전문 전송중으로 변경
        mapper.updateEtrSending(seq)

        // 타행 계좌 명의 조회
        khttp.async.get(
                url = "$apUrl/ext/acct",
                timeout = 1.0,
                params = mapOf("acctNo" to input.acctNo, "bankCd" to input.bankCd, "name" to input.userNm),
                onError = {
                    when (message) {
                        "Read timed out" -> {
                            log.warn("T1 전문: [$seq] 타임아웃 발생")
                            mapper.updateEtrTimeout(seq)                  // 대외 전문 타임아웃으로 변경
                        }
                        else -> {
                            servicePush.regPush(PUSH_ID.P1, input.userId) // P1: 타행 계좌의 명의 정보를 얻어오는데 실패 하였습니다. 잠시 후 재시도 해주세요.
                            mapper.updateEtrErr(seq)                      // 대외 전문 에러로 변경
                        }
                    }
                }) {

            val resNm = JSONObject(text)["name"].toString()
            log.debug("T1 전문: [ $seq ] 고객명 수신: $resNm")

            // 타행 계좌 조회 수신 처리
            val result = serviceAcct.recvT1(resNm, input.userId, input.bankCd, input.acctNo)

            if (result) {                    // IF 타행 계좌 조회 전문 수신 처리가 정상
                mapper.updateEtrSuccess(seq) //   대외 전문 성공로 변경
            } else {                         // ELSE
                mapper.updateEtrErr(seq)     //   대외 전문 에러로 변경
            }

        }

    }

    /**
     * T2 전문 처리. Future Return.
     * - 호출 시 같은 사용자의 전문만 들어옵니다.
     */
    fun requestT2(listInput: MutableList<RequestT2In>): Future<Any> = CompletableFuture.supplyAsync {

        // Init --------------------------------------------------------------------------------------------------------
        val listCancelTarget = mutableListOf<Int>() // 취소 처리할 전문의 seq 리스트

        // 전문 처리 시작
        val listSorted = listInput.sortedBy { it.seq }                      // 전문 순번으로 정렬
        listSorted.forEachIndexed { idx, input ->                           // LOOP IN 특정 고객의 당발 전문 리스트
            if (idx != 0) {                                                 //   첫 전문 외에는
                listCancelTarget.add(input.seq)                             //   처리하지 않고, 취소 처리할 리스트에 추가
                return@forEachIndexed
            }

            log.debug("T2 전문:[ ${input.seq} ] 처리 시작")

            // validation: 해당 사용자의 단계가 B3[1원 이체 시작] 이상인가?
            val stgCd = serviceAcct.getAcctStgCd(input.body.userId)
            if (stgCd >= "B3") {                                              // IF 인증단계 [B3:이체] 대기중 이상이라면
                mapper.updateOneEtrCancelBySeq(input.seq)                     //   전문 상태 [04:취소] 처리
                return@supplyAsync                                            //   해당 고객 전문 처리는 종료
            }

            // validation: 전송중이거나 정상 처리된 사용자의 이체 처리 전문이 있는가?
            if (mapper.selectOneForValid(TR_ID.T2.name, input.body.userId)) { // IF 전송 중인 전문이 있다면
                mapper.updateOneEtrCancelBySeq(input.seq)                     //   전문 상태 [04:취소] 처리
                return@supplyAsync                                            //   해당 고객 전문 처리 종료
            }

            // Main ----------------------------------------------------------------------------------------------------
            serviceAcct.updateAcctStgByFlow(input.body.userId, "B3")  // 인증 진행 단계 변경 [B3: 1원 이체 시작]

            mapper.updateEtrSending(input.seq)                        // 대외 전문 상태 [02:전송중] 으로 변경

            // main: 계좌 이체 전문 전송
            khttp.async.post(
                    url = "$apUrl/ext/acct",
                    timeout = 1.0,
                    json = mapOf("acctNo" to input.body.acctNo,
                            "bankCd" to input.body.bankCd,
                            "etcCtn" to input.body.etcCtn),
                    onError = {
                        when (message) {
                            "Read timed out" -> {                  // WHEN 에러가 타임아웃 때문이라면
                                mapper.updateEtrTimeout(input.seq) //  대외 전문 상태 [03:타임아웃]으로 변경
                                log.warn("T2 전문: [${input.seq}] 타임아웃 발생")

                                // [03:타임아웃] 되었지만, 특정 사용자의 T2 전문만이 유효한 것이라면 인증단계코드를 [B2:이체 대기] 로 돌린다.
                                if (mapper.selectOneIsOnlyValidMe(input.seq, TR_ID.T2.name, input.body.userId))
                                    serviceAcct.updateAcctStgByFlow(input.body.userId, "B2", true)
                            }
                            else -> {                                              // 타임아웃이 아닌 다른 에러라면
                                servicePush.regPush(PUSH_ID.P2, input.body.userId) //   P2: 다른 계좌로 시도 해 주세요.
                                mapper.updateEtrErr(input.seq)                     // 대외 전문 상태 [05:수신], 결과코드 [02:에러]로 변경
                            }
                        }

                    }) {


                // 타행 계좌 조회 수신 처리
                val etcCtn = JSONObject(text)["etcCtn"].toString()
                val result = serviceAcct.recvT2(etcCtn, input.body)

                if (result) {                          // IF 수신 후 처리 결과가 정상
                    mapper.updateEtrSuccess(input.seq) //   대외 전문 상태 [05:수신], 결과코드 [01:성공]로 변경
                } else {                               // ELSE
                    mapper.updateEtrErr(input.seq)     //   대외 전문 상태 [05:수신], 결과코드 [02:에러]로 변경
                }
            }
        }

        // 취소 처리 대상 전문 업데이트: 사용자별 전문 중, 현 시점 조회 된 첫번째를 제외한 모든 전문이 취소 할 대상 입니다.
        if (listCancelTarget.size > 0)
            mapper.updateListEtrCancelBySeqList(listCancelTarget) // 대외 전문 상태 [04:수신] 으로 변경

        return@supplyAsync

    }

    data class RequestT2In(
            val seq: Int,
            val body: T2Body
    )

}
