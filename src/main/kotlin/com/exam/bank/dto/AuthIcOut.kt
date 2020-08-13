package com.exam.bank.dto

data class AuthIcOut(
        val result: String // 결과
        , val name: String // 성명
        , val no: String   // 신분증 번호
        , val ci: String   // CI
)
