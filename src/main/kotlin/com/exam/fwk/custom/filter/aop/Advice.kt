package com.exam.fwk.custom.filter.aop

import ch.qos.logback.classic.Logger
import com.exam.bank.service.AuthService
import com.exam.fwk.core.base.BaseException
import com.exam.fwk.core.component.Area
import com.exam.fwk.core.error.UnauthorizedException
import com.exam.fwk.custom.service.TransactionService
import com.exam.fwk.custom.util.DateUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.net.URI
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.system.measureTimeMillis

@Aspect
@Component
class Advice {

    companion object {
        val log: Logger = LoggerFactory.getLogger(Advice::class.java) as Logger
    }

    @Autowired lateinit var area: Area                             // Common 영역
    @Autowired lateinit var serviceTransaction: TransactionService // 거래내역 서비스
    @Autowired lateinit var serviceAuth: AuthService               // 인증 서비스

    /**
     * 콘트롤러 전/후 처리
     */
    @Around("PointcutList.allController()")
    fun aroundController(pjp: ProceedingJoinPoint): Any? {

        // Init --------------------------------------------------------------------------------------------------------
        var result: Any? = null
        val req = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val commons = area.commons
        val signatureName = "${pjp.signature.declaringType.simpleName}.${pjp.signature.name}"

        setAuth(req)       // 사용자 인증 처리
        setCommonArea(req) // CommonArea 설정
        validPermission()  // 접근 권한 처리

        // Main --------------------------------------------------------------------------------------------------------
        log.info("[${commons.gid}] >>>>>  controller start [$signatureName() from [${req.remoteAddr}] by ${req.method} ${req.requestURI}")
        try {
            area.commons.elapsed = measureTimeMillis {
                result = pjp.proceed()
            }
        } catch (e: Exception) {
            log.info("[${commons.gid}] <<<<<  controller   end [$signatureName() from [${commons.remoteIp}] [${commons.elapsed}ms] with Error [${e.javaClass.simpleName}]")
            saveTransaction(e) // 거래내역 저장
            throw e
        }

        // End ---------------------------------------------------------------------------------------------------------
        saveTransaction() // 거래내역 저장

        log.info("[${commons.gid}] <<<<<  controller   end [$signatureName() from [${commons.remoteIp}] [${commons.elapsed}ms]")
        return result

    }

    /**
     * 사용자 인증 처리
     */
    fun setAuth(req: HttpServletRequest) {

        // JWT 로부터 CommonUser 를 decode
        val jwt = req.getHeader("authorization") ?: return

        val user = serviceAuth.decodeToken(jwt) ?: return

        area.commons.user = user

    }


    /**
     * Common Area 셋팅
     */
    private fun setCommonArea(req: HttpServletRequest) {

        val commons = area.commons
        commons.startDt = OffsetDateTime.now(ZoneId.of("+9"))
        commons.date = DateUtils.currentDate()
        commons.gid = UUID.randomUUID().toString()
        commons.path = req.requestURI
        commons.remoteIp = req.remoteAddr
        commons.queryString = req.queryString
        commons.method = req.method

        if (req.getHeader("referer") != null) {
            val referrer = req.getHeader("referer")
            commons.referrer = URI(referrer).path
        }

//        val isExcept = req.contentType.toLowerCase().indexOf("multipart/form-data") > -1
//        if (req.method in arrayOf("POST", "PATCH", "DELETE") && !isExcept) {
//            var body = IOUtils.toString(req.inputStream, Charset.forName("UTF-8"))
//            if (body.isNotEmpty()) {
//                if (body.length > 4000) {
//                    body = body.substring(0..4000)
//                }
//                body = org.apache.commons.lang3.StringUtils.chomp(body)
//                body = body.replace("\n", "")
//                commons.body = body
//            }
//        }

    }

    /**
     * 사용자 인증
     * - 권한이 있는 접근인지 검사
     */
    private fun validPermission() {

        // 예외 URL 인지 체크 한다.
        val path = area.commons.path
        val isExceptUrl = when {
            path == "/" -> true
            path == "/error" -> true
            path == "/ping" -> true
            path == "/auth/sign-in" -> true
            path == "/tr" -> true
            path == "/tr-stat" -> true
            path.startsWith("/tmp") -> true
            else -> false
        }

        if (isExceptUrl)
            return

        // 로그인이 되어있는지 체크
        if (area.commons.user == null) {
            log.debug("validPermission:[$path] not authorized")
            throw UnauthorizedException()
        }

    }

    /**
     * 거래내역 저장
     */
    private fun saveTransaction(ex: Exception? = null) {

        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val response = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).response as HttpServletResponse
        val commons = area.commons
        commons.err = ex
        commons.endDt = OffsetDateTime.now(ZoneId.of("+9"))
        commons.elapsed = Duration.between(commons.startDt, commons.endDt).toMillis()


        commons.statCd = response.status.toString()

        val originalUri = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI)
        originalUri?.let { commons.path = originalUri.toString() }

        ex?.let {
            // error message
            val errorStack: ArrayList<String> = arrayListOf()
            var cause: Throwable = ex
            while (cause.cause != null) {
                errorStack.add(cause.cause!!.message.toString())
                cause = cause.cause!!
            }
            commons.errMsg = cause.message

            // status code
            if (ex is BaseException) commons.statCd = ex.httpStatus.value().toString()
            else commons.statCd = "500"
        }

        serviceTransaction.insertTransaction(commons)
    }

    /**
     * 서비스 전/후 처리
     */
    @Around("PointcutList.allServices()")
    fun aroundService(pjp: ProceedingJoinPoint): Any? {

        // Init --------------------------------------------------------------------------------------------------------
        var result: Any? = null
        val serviceFullName = (pjp.signature.declaringType.simpleName + "." + pjp.signature.name)
        val args = pjp.args.toList().joinToString(",")
        var elapsed: Long = 0
        val withArgs = if (args.isNotEmpty()) {
            if (args.length > 120) "with ${args.slice(0..120)}..."
            "with $args"
        } else ""

        log.info(" >>>>>  service start   [$serviceFullName()] $withArgs ")

        // Main --------------------------------------------------------------------------------------------------------
        try {
            elapsed = measureTimeMillis {
                result = pjp.proceed()
            }
        } catch (e: Exception) {
            log.error("     >  [$serviceFullName()] occurred error {${e.message}}]")
            throw e
        } finally {
            val returnForLog = when {
                result != null && result.toString().length > 120 -> "{ ${result.toString()
                        .slice(0..120)}...}"
                result != null -> "{ $result }"
                else -> ""
            }

            log.info(" >>>>>  service   end   [$serviceFullName()] [${elapsed}ms] $returnForLog")
        }

        // End ---------------------------------------------------------------------------------------------------------
        return result

    }

}
