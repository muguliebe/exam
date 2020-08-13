package com.exam.bank.entity

import javax.persistence.*

@Entity
@Table(name = "com_user_mst")
data class ComUserMst(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var userId: Int      = 0  // 사용자 ID
        , var email: String  = "" // 이메일
        , var userNm: String = "" // 성명
)
