package com.exam.bank.task

import com.exam.bank.dto.T1Body
import com.exam.bank.dto.T2Body
import com.exam.bank.repo.jpa.EtrRepo
import com.exam.bank.repo.mybatis.EtrMapper
import com.exam.bank.service.AcctService
import com.exam.bank.service.ExtService
import com.exam.fwk.core.base.BaseService
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.Future

/**
 * 전문 요청 Task
 */
@Service
class ExtTaskService : BaseService() {

    @Autowired lateinit var mapper: EtrMapper        // 대외 전문 마스터 매퍼(MyBatis)
    @Autowired lateinit var repo: EtrRepo            // 대외 전문 마스터 레포(JPA)
    @Autowired lateinit var serviceAcct: AcctService // 계좌 서비스
    @Autowired lateinit var serviceExt: ExtService   // 대외거래 서비스

    /**
     * T1 - 타행 계좌 명의 조회 전문 처리
     */
    @Scheduled(fixedRate = 3000)
    fun scheduleT1() {

        // 발송 대상 조회
        mapper.selectListReqTr("T1") // T1: 계좌 명의 조회
                .forEach {
                    val body = Gson().fromJson(it.body.toString(), T1Body::class.java)
                    serviceExt.requestT1(it.seq, body) // 전문 처리
                }
    }

    /**
     * T2 - 타행 계좌 이체 전문 처리
     * - 발송 대상 조회
     * - 사용자별로 그룹핑 하여, T2 송신 병렬 처리
     */
    @Scheduled(fixedRate = 3000)
    fun scheduleT2() {

        // 발송 대상 조회
        val listTarget = mapper.selectListReqTr("T2") // T2: 1원 계좌 이체
                .groupBy { { it.seq } }

        val listFuture = mutableListOf<Future<Any>>()
        listTarget.forEach { // 사용자별 그룹핑된 전문 리스트

            // 사용자별 전문을, 호출할 서비스의 인풋에 맞게 리스트와 합니다.
            val inTReqT2 = it.value.map { each ->
                val body = Gson().fromJson(each.body, T2Body::class.java)
                ExtService.RequestT2In(each.seq, body)
            }.toMutableList()

            val future = serviceExt.requestT2(inTReqT2) // T2 전문 처리 서비스 (Async)
            listFuture.add(future)
        }

        listFuture.forEach { it.get() }


    }


}
