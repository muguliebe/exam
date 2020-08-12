package com.exam.ap

import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 외부 AP 로 간주
 */
@RestController
@RequestMapping("/ap")
class ApController {

    val log = LoggerFactory.getLogger(ApController::class.java) as Logger

    @GetMapping("/ext/acct")
    fun getAcctInfo(acctNo: String) : String {
        return "acct"
    }

}
