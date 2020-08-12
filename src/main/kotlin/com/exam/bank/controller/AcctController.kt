package com.exam.bank.controller

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

    @Autowired lateinit var serviceAuth: AuthService

    /**
     * 로그인
     */
    @PostMapping
    fun signIn(@RequestBody input: SignInIn) = serviceAuth.signIn(input.email!!)

    data class SignInIn(
            val email: String? = null
    )
}
