package com.exam.fwk.custom.pojo

import java.time.OffsetDateTime

data class CommonArea(
        var date: String = "",
        var gid: String = "",
        var referrer: String? = null,
        var method: String = "",
        var path: String = "",
        var statCd: String? = null,
        var startDt: OffsetDateTime? = null,
        var endDt: OffsetDateTime? = null,
        var elapsed: Long? = 0,
        var remoteIp: String = "",
        var queryString: String? = null,
        var body: String? = null,
        var err: Exception? = null,
        var errMsg: String? = null,
        var user: ComUser? = null
)
