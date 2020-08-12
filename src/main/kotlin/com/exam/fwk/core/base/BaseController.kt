package com.exam.fwk.core.base

import ch.qos.logback.classic.Logger
import com.exam.fwk.core.component.Area
import com.exam.fwk.core.error.BizException
import com.exam.fwk.core.error.DefaultExceptionResponse
import com.exam.fwk.core.error.UnauthorizedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.BadSqlGrammarException
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest

@Transactional
class BaseController : BaseObject() {

    protected final val log = LoggerFactory.getLogger(this::class.java) as Logger

    fun makeResponse(status: HttpStatus, exception: Exception, request: HttpServletRequest, area: Area): ResponseEntity<DefaultExceptionResponse> {
        val response = DefaultExceptionResponse(request, exception)
        response.status = status.value().toString()
        response.statusName = status.name
        response.errorMessageCd = exception.message
        response.gid = area.commons.gid

        // 에러 유형 지정
        response.errorType = when (exception) {
            is BizException -> "B"
            is BaseException -> "D"
            is BadSqlGrammarException -> "D"
            else -> "S"
        }

        // 에러 메시지 코드 변환
        if (exception is BaseException)
            response.errorMessage = exception.msgCd
        else
            response.errorMessage = exception.message

        response.errorClassName = exception.javaClass.simpleName
        var cause: Throwable = exception
        while (cause.cause != null) {
            if (cause.cause is BaseException) {
                val innerCause = cause.cause as BaseException
                innerCause.message?.let {
                    response.errorStack.add(innerCause.message!!)
                }
                // 에러 유형은 최초 발생한 에러로 재지정
                response.errorType = when (exception) {
                    is BizException -> "B"
                    is UnauthorizedException -> "B"
                    is BaseException -> "D"
                    is BadSqlGrammarException -> "D"
                    else -> "S"
                }
            } else {
                response.errorStack.add(cause.cause!!.message.toString())
            }
            cause = cause.cause!!
        }

        return ResponseEntity.status(status).body(response)
    }

}
