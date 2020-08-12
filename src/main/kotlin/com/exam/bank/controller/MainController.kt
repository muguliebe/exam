package com.exam.bank.controller

import com.exam.fwk.core.base.BaseController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class MainController : BaseController() {

    @GetMapping
    fun ping() = "pong"

}
