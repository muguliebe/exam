package com.exam.bank.controller

import com.exam.bank.dto.AuthIcFirstIn
import com.exam.bank.dto.AuthIcSecondIn
import com.exam.bank.dto.T1Body
import com.exam.bank.service.AcctService
import com.exam.fwk.core.base.BaseController
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/acct")
@Api(description = "계좌")
class AcctController : BaseController() {

    @Autowired lateinit var serviceAcct: AcctService // 계좌 서비스

    @GetMapping("/auth-stage")
    @ApiOperation(value = "계좌개설 단계 조회")
    fun getCreateStage(): String = serviceAcct.getAcctStgCd(area.commons.user!!.userId)

    @PostMapping("/auth/a1")
    @ApiOperation(value = "신분증 인증 1단계(이미지 제출)")
    fun authIcFirst(@RequestBody input: AuthIcFirstIn) = serviceAcct.authIcFirst(input)

    @PostMapping("/auth/a2")
    @ApiOperation(value = "신분증 인증 2단계(교정본 제출)")
    fun authIcSecond(@RequestBody input: AuthIcSecondIn) = serviceAcct.authIcSecond(input)

    @PostMapping("/auth/b1")
    @ApiOperation(value = "타행 계좌 인증 요청")
    fun procExtAcctAuth(@RequestBody input: T1Body) = serviceAcct.procExtAcctAuth(input)

    @PostMapping("/auth/c1")
    @ApiOperation(value = "단어 인증")
    fun authWord(@RequestBody input: AcctService.AuthWordIn) = serviceAcct.authWord(input)


}
