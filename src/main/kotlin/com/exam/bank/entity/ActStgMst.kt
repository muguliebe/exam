package com.exam.bank.entity

import org.springframework.context.annotation.Description
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "act_stg_mst")
@Description("인증단계 마스터")
data class ActStgMst(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var userId: Int = 0                       // 사용자 ID
        , var acctStgCd: String  = ""             // 인증단계코드
        , var etcCtn: String = ""                 // 적요 (인증단어)
        , var createUserId: Int? = null           // 생성자 ID
        , var createDt: OffsetDateTime? = null    // 생성 일시
        , var updateUserId: Int? = null           // 수정자 ID
        , var updateDt: OffsetDateTime? = null    // 수정 일시
)
