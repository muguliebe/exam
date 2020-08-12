package com.exam.bank.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TransactionHst(
        var transactionDate: LocalDate = LocalDate.now(),
        var gid: String = "",
        var method: String = "",
        var path: String = "",
        var startTime: String? = null,
        var endTime: String? = null,
        var elapsed: Long? = null,
        var referrer: String? = null,
        var remoteIp: String = "",
        var statCode: String? = null,
        var queryString: String? = null,
        var body: String? = null,
        var errMsg: String? = null,
        var createUserId: String? = null,
        var createDt: OffsetDateTime? = null
)
