package com.exam.bank.dto

data class T2Body(
        val bankCd: String   // 은행 코드
        , val acctNo: String // 계좌 번호
        , val etcCtn: String // 적요 내용
        , val userId: Int    // 당행 사용자 ID
)
