package com.exam.bank.controller

import com.exam.fwk.core.base.BaseController
import com.exam.fwk.custom.service.TransactionService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(description = "메인")
class MainController : BaseController() {

    @Autowired lateinit var serviceTr: TransactionService // 거래내역 서비스

    @GetMapping
    @ApiOperation(value = "핑퐁")
    fun ping() = "pong"

    @GetMapping("/tr")
    @ApiOperation(value = "거래내역 조회")
    fun getTr() = serviceTr.getTransactions()

    @GetMapping("/tr-stat")
    @ApiOperation(value = "거래내역 인증 월별 집계")
    fun getTrAuthStat() = serviceTr.getTrAuthStat()

}
