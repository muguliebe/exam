package com.exam.bank.controller

import com.exam.bank.dto.GetTmpListOut
import com.exam.bank.service.TmpService
import com.exam.fwk.core.base.BaseController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * 구조 테스트용 콘트롤러
 */
@RestController
@RequestMapping("/tmp")
class TmpController : BaseController() {

    @Autowired lateinit var serviceTmp: TmpService
    @Value("\${server.port}") var port:Int = 0

    @GetMapping("/test")
    fun testTmpOut(): List<GetTmpListOut> = serviceTmp.getTmpList()

    @GetMapping("/tr")
    fun getTr() = serviceTmp.getTrList()

    @GetMapping("/err")
    fun getErr() = serviceTmp.getError()

    @GetMapping("/ap")
    fun callAp() {
        val result = khttp.get(
                "http://localhost:${port}/ap/ext/acct"
        ,params = mapOf("acctNo" to "123")
        )

        log.info("result=${result.text}")
    }

}
