package com.exam.bank.entity

import org.springframework.context.annotation.Description
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "com_push_mst")
@Description("푸쉬 마스터")
data class ComPushMst(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var seq: Int = 0                       // 순번
        , var pushId: String = ""              // PUSH ID
        , var userId: Int = 0                  // 사용자 ID
        , var createUserId: Int? = null        // 생성자 ID
        , var createDt: OffsetDateTime? = null // 생성 일시
)
