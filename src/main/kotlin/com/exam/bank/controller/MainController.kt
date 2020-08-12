package com.exam.bank.controller

import com.exam.fwk.core.base.BaseController
import com.exam.fwk.custom.service.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 메인 콘트롤러
 */
@RestController
class MainController : BaseController() {

    @Autowired lateinit var serviceTr: TransactionService // 거래내역 서비스

    @GetMapping
    fun ping() = "pong"

    @GetMapping("/tr")
    fun getTr() = serviceTr.getTransactions()

}
