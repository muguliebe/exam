package com.exam.fwk.core.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext

abstract class BaseObject {

    @Autowired lateinit var ctx: ApplicationContext

    @Value("\${server.port}") var port: Int = 0

    val apUrl
        get() = "http://localhost:${port}/ap"

}
