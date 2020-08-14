package com.exam.bank.controller

import com.exam.bank.dto.GetTmpListOut
import com.exam.bank.service.TmpService
import com.exam.fwk.core.base.BaseController
import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/tmp")
@Api(description = "구조 테스트용 콘트롤러")
class TmpController() : BaseController() {

    @Autowired lateinit var serviceTmp: TmpService

    @GetMapping("/test")
    fun testTmpOut(): List<GetTmpListOut> = serviceTmp.getTmpList()

    @GetMapping("/tr")
    fun getTr() = serviceTmp.getTrList()

    @GetMapping("/err")
    fun getErr() = serviceTmp.getError()

    @GetMapping("/jpa")
    fun getUser() = serviceTmp.testJpa()

    @GetMapping("/ap")
    fun callAp() {
        val result = khttp.get(
                "$apUrl/ap/ext/acct"
                , params = mapOf("acctNo" to "123")
        )

        log.info("result=${result.text}")
    }

}
