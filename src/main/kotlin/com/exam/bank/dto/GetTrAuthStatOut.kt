package com.exam.bank.dto

data class GetTrAuthStatOut(
          var statMm: String = ""        // 집계 월
        , var cntSuccessId: Long = 0     // 신분증 성공 건수
        , var cntFailId: Long = 0        // 신분증 실패 건수
        , var cntSuccessSend: Long = 0   // 이체 성공 건수
        , var cntFailSend: Long = 0      // 이체 실패 건수
        , var cntSuccessWord: Long = 0   // 단어 성공 건수
        , var cntFailWord: Long = 0      // 단어 실패 건수
)
