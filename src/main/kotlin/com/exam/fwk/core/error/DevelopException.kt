package com.exam.fwk.core.error

import com.exam.fwk.core.base.BaseException
import org.springframework.http.HttpStatus

/**
 * 개발자 에러일 경우 사용
 */
open class DevelopException(
        override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
        override val msgCd: String,
        e: Throwable? = null,
        override val msgArgs: List<String>? = null
) : BaseException(httpStatus, msgCd, e) {
    constructor(e: Throwable, msgCd: String) : this(HttpStatus.BAD_REQUEST, msgCd, e)
    constructor(e: Throwable, msgCd: String, msgArgs: List<String>?) : this(HttpStatus.BAD_REQUEST, msgCd, e, msgArgs)
    constructor(msgCd: String) : this(HttpStatus.BAD_REQUEST, msgCd)
    constructor(msgCd: String, msgArgs: List<String>?) : this(HttpStatus.BAD_REQUEST, msgCd, msgArgs = msgArgs)
}
