package com.exam.bank.dto

data class T1Body(
        var bankCd: String   = ""   // 은행 코드
        , var acctNo: String = ""   // 계좌 번호
        , var userId: Int    = 0    // 당행 사용자 ID
        , var userNm: String = ""   // 당행 사용자명
)
