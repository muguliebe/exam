package com.exam.bank.controller

import com.exam.bank.service.AcctService
import com.exam.bank.service.AuthService
import com.exam.fwk.core.base.BaseController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


/**
 * 계좌 콘트롤러
 */
@RestController
@RequestMapping("/acct")
class AcctController : BaseController() {

    @Autowired lateinit var serviceAcct: AcctService

    /**
     * 계좌개설 단계 조회
     */
    @GetMapping("/create-stage")
    fun getCreateStage(): String = serviceAcct.getAcctStgCd(area.commons.user!!.userId)

}
