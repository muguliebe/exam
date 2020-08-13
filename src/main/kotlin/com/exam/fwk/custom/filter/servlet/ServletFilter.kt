package com.exam.fwk.custom.filter.servlet

import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import javax.servlet.*
import javax.servlet.http.HttpServletRequest

/**
 * Servlet Filter
 * - HttpSerrvletRequest 를 HttpRequestWrapper 로 변환
 * - 스트림 재사용 위함
 */
@Configuration
class ServletFilter : Filter {
    companion object {
        private val log = LoggerFactory.getLogger(ServletFilter::class.java) as Logger
    }

    override fun init(filterConfig: FilterConfig?) {
        log.warn("ServletFilter] Initialize Called")
        super.init(filterConfig)
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val requestWrapper = HttpRequestWrapper(request as HttpServletRequest)
        chain.doFilter(requestWrapper, response)
    }

}
