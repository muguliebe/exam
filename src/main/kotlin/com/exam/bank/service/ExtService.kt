package com.exam.bank.service

import com.exam.bank.dto.T1Body
import com.exam.bank.dto.T2Body
import com.exam.bank.entity.ComEtrMst
import com.exam.bank.repo.jpa.EtrRepo
import com.exam.bank.repo.mybatis.EtrMapper
import com.exam.bank.service.PushService.PUSH_ID
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.custom.util.DateUtils
import com.google.gson.Gson
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
        inSave.trId = trId.name
        inSave.trDy = DateUtils.nowDate()
        inSave.trStatCd = "01"                           // 거래상태코드 [01:요청, 02: 전송, 03:타임아웃, 04:취소, 05:수신]
        inSave.trResCd = "00"                            // 거래결과코드 [00:진행중, 01:성공, 02:실패]
        inSave.userId = userId
        inSave.createDt = DateUtils.currentTimeStamp()
        inSave.createUserId = userId
        inSave.body = body

        mapper.insertEtr(inSave)
    }

    /**
     * 전문 요청 취소
     * - 입력받은 전문 ID, 사용자 ID 와 매칭되는 모든 전문을 cancel 한다.
     * - 거래상태코드 [02:전송, 04:취소, 05:완료] 인 경우 제외 한다.
     */
    fun cancelEtr(trId: TR_ID, userId: Int) = mapper.updateListEtrCancel(trId.toString(), userId)


    /**
     * T1 전문 처리
     */
    fun requestT1(seq: Int, input: T1Body) {

        log.info("T1 전문 처리 시작: $seq")

        // validation: 해당 사용자의 단계가 B2[이체 요청] 이상인가?
        val stgCd = serviceAcct.getAcctStgCd(input.userId)
        if (stgCd >= "B2") {
            log.info("T1 전문:[ $seq ] 해당 사용자 B2 로 종료. [04:취소] 처리")
            mapper.updateOneEtrCancelBySeq(seq)
            return
        }

        mapper.updateEtrSending(seq) // 대외 전문 전송중으로 변경

        // 타행 계좌 명의 조회
        khttp.async.get(
                url = "$apUrl/ext/acct",
                timeout = 1.0,
                params = mapOf("acctNo" to input.acctNo, "bankCd" to input.bankCd, "name" to input.userNm),
                onError = {
                    when (message) {
                        "Read timed out" -> {
                            mapper.updateEtrTimeout(seq)                  // 대외 전문 타임아웃으로 변경
                        }
                        else -> {
                            servicePush.regPush(PUSH_ID.P1, input.userId) // P1: 타행 계좌의 명의 정보를 얻어오는데 실패 하였습니다. 잠시 후 재시도 해주세요.
                            mapper.updateEtrErr(seq)                      // 대외 전문 에러로 변경
                        }
                    }
                }) {

            val resNm = JSONObject(text)["name"].toString()
            log.info("T1 전문: [ $seq ] 고객명 수신: $resNm")

            // 타행 계좌 조회 수신 처리
            val result = serviceAcct.recvT1(resNm, input.userId, input.bankCd, input.acctNo)

            if (result) {
                mapper.updateEtrSuccess(seq) // 대외 전문 성공로 변경
            } else {
                mapper.updateEtrErr(seq)     // 대외 전문 에러로 변경
            }

        }

    }

    /**
     * T2 전문 처리. Future Return.
     * - 호출 시 같은 사용자의 전문만 들어옵니다.
     */
    fun requestT2(listInput: MutableList<RequestT2In>): Future<Any> = CompletableFuture.supplyAsync {

        // init
        val listCancelTarget = mutableListOf<Int>() // 취소 처리할 전문의 seq 리스트

        // 전문 처리 시작
        val listSorted = listInput.sortedBy { it.seq }
        listSorted.forEachIndexed { idx, input ->
            if (idx != 0) {
                listCancelTarget.add(input.seq) // 취소 처리할 전문에 추가
                return@forEachIndexed
            }

            log.info("T2 전문:[ ${input.seq} ] 처리 시작")

            // validation: 해당 사용자의 단계가 B3[1원 이체 시작] 이상인가?
            val stgCd = serviceAcct.getAcctStgCd(input.body.userId)
            if (stgCd >= "B3") {
                log.warn("T2 전문:[ ${input.seq} ] 해당 사용자 B3 로 종료. [04:취소] 처리")
                mapper.updateOneEtrCancelBySeq(input.seq)
                return@supplyAsync
            }

            // validation: 전송중이거나 정상 처리된 사용자의 이체 처리 전문이 있는가?
            if (mapper.selectOneForValid(TR_ID.T2.name, input.body.userId)) {
                log.warn("T2 전문:[ ${input.seq} ]: 전송중인 전문 존재하여 종료. [04:취소] 처리")
                mapper.updateOneEtrCancelBySeq(input.seq)
                return@supplyAsync
            }

            // 인증 진행 단계 변경 [B3: 1원 이체 시작]
            serviceAcct.updateAcctStgByFlow(input.body.userId, "B3")

            // 대외 전문 전송중으로 변경
            log.info("T2 전문:[ ${input.seq} ]: 전송중 '02' 로 변경")
            mapper.updateEtrSending(input.seq)

            // main: 계좌 이체 전문 전송
            khttp.async.post(
                    url = "$apUrl/ext/acct",
                    timeout = 1.0,
                    json = mapOf("acctNo" to input.body.acctNo,
                            "bankCd" to input.body.bankCd,
                            "etcCtn" to input.body.etcCtn),
                    onError = {
                        when (message) {
                            "Read timed out" -> {
                                log.info("T2 전문:[ ${input.seq} ]: 03:타임아웃 변경")
                                mapper.updateEtrTimeout(input.seq)                // 대외 전문 타임아웃으로 변경

                                // 타임아웃 되었음으로, 특정 사용자의 T2 전문이 유일한 것이라면 인증단계코드를 B2 로 돌린다.
                                if( mapper.selectOneIsOnlyValidMe(input.seq, TR_ID.T2.name, input.body.userId) ) {
                                    serviceAcct.updateAcctStgByFlow(input.body.userId, "B2", true)
                                    log.info("T2 전문:[ ${input.seq} ]: 03:타임아웃 이지만, 유일한 전문으로 B2 단계로 변경")
                                }

                            }
                            else -> {
                                servicePush.regPush(PUSH_ID.P2, input.body.userId) // P2: 다른 계좌로 시도 해 주세요.
                                log.info("T2 전문:[ ${input.seq} ]: 05:수신 변경, 에러 처리")
                                mapper.updateEtrErr(input.seq)                     // 대외 전문 에러로 변경
                            }
                        }

                    }) {

                val etcCtn = JSONObject(text)["etcCtn"].toString()
                log.info("T2 전문:[ ${input.seq} ]: 적요 수신: $etcCtn")

                // 타행 계좌 조회 수신 처리
                val result = serviceAcct.recvT2(etcCtn, input.body)

                if (result) {
                    log.info("T2 전문:[ ${input.seq} ]: 05:수신 변경, 정상 처리")
                    mapper.updateEtrSuccess(input.seq) // 대외 전문 성공로 변경
                } else {
                    log.info("T2 전문:[ ${input.seq} ]: 05:수신 변경, 에러 처리")
                    mapper.updateEtrErr(input.seq)     // 대외 전문 에러로 변경
                }
            }
        }

        // 취소 처리 대상 전문 업데이트: 사용자별 전문 중, 현 시점 조회 된 첫번째를 제외한 모든 전문이 취소 할 대상 입니다.
        if (listCancelTarget.size > 0) {
            log.info("T2 전문: 최초 대상 건 외 [04:취소] 처리")
            mapper.updateListEtrCancelBySeqList(listCancelTarget)
        }

        return@supplyAsync
    }

    data class RequestT2In(
            val seq: Int,
            val body: T2Body
    )

}
