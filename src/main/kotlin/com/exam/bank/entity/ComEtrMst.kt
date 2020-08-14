package com.exam.bank.entity

import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "com_etr_mst")
data class ComEtrMst(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var seq: Int = 0                          // 순번
        , var trDy: Date? = null                  // 거래 일자
        , var trId: String = ""                   // 거래 ID
        , var trStatCd: String = ""               // 거래상태코드 [01:요청, 02: 전송, 03:타임아웃, 04:취소, 05:수신]
        , var trResCd: String = ""                // 거래결과코드 [01:성공, 02:실패]
        , var userId: Int? = null                 // 사용자 ID
        , var body: String = ""                   // 전문 입력
        , var createUserId: Int? = null           // 생성자 ID
        , var createDt: OffsetDateTime? = null    // 생성 일시
        , var updateUserId: Int? = null           // 수정자 ID
        , var updateDt: OffsetDateTime? = null    // 수정 일시
)
