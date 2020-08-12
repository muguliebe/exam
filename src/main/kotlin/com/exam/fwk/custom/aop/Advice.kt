package com.exam.fwk.custom.aop

import ch.qos.logback.classic.Logger
import com.exam.fwk.core.base.BaseException
import com.exam.fwk.core.component.Area
import com.exam.fwk.custom.service.TransactionService
import com.exam.fwk.custom.util.DateUtils
import org.apache.commons.io.IOUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.net.URI
import java.nio.charset.Charset
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

    @Autowired lateinit var area: Area // Common 영역
    @Autowired lateinit var serviceTransaction: TransactionService // Common 영역

    /**
     * 콘트롤러 전/후 처리
     */
    @Around("PointcutList.allController()")
    fun aroundController(pjp: ProceedingJoinPoint): Any? {

        // Init --------------------------------------------------------------------------------------------------------
        var result: Any? = null
        val commons = area.commons
        val req = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val signatureName = "${pjp.signature.declaringType.simpleName}.${pjp.signature.name}"

        // Set Common Area ---------------------------------------------------------------------------------------------
        setCommonArea(req)

        // Main --------------------------------------------------------------------------------------------------------
        log.info("[${commons.gid}] >>>>>  controller start [$signatureName() from [${req.remoteAddr}] by ${req.method} ${req.requestURI}")
        try {
            val elapsed = measureTimeMillis {
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

        if (req.method in arrayOf("POST", "PATCH", "DELETE") && commons.path != "upload-edm-files") {
            var body = IOUtils.toString(req.inputStream, Charset.forName("UTF-8"))
            if (body.isNotEmpty()) {
                if (body.length > 4000) {
                    body = body.substring(0..4000)
                }
                body = org.apache.commons.lang3.StringUtils.chomp(body)
                body = body.replace("\n", "")
                commons.body = body
            }
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
        val commons = area.commons
        val serviceFullName = (pjp.signature.declaringType.simpleName + "." + pjp.signature.name)
        val args = pjp.args.toList().joinToString(",")
        var elapsed: Long = 0
        val withArgs = if (args.isNotEmpty()) {
            if (args.length > 120) "with ${args.slice(0..120)}..."
            "with $args"
        } else ""

        log.info("[${commons.gid}] >>>>>  service start   [$serviceFullName()] $withArgs ")

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

            log.info("[${commons.gid}] >>>>>  service   end   [$serviceFullName()] [${elapsed}ms] $returnForLog")
        }

        // End ---------------------------------------------------------------------------------------------------------
        return result

    }

}
