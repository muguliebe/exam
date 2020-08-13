package com.exam.bank.controller

import com.exam.bank.service.AuthService
import com.exam.fwk.core.base.BaseController
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/auth")
@Api(description = "인증 콘트롤러")
class AuthController : BaseController() {

    @Autowired lateinit var serviceAuth: AuthService

    @PostMapping("/sign-in")
    @ApiOperation(value = "로그인")
    fun signIn(@RequestBody input: SignInIn) = serviceAuth.signIn(input.email!!)

    data class SignInIn(
            val email: String? = null
    )
}
