package com.exam.fwk.core.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

abstract class BaseObject {

    @Autowired lateinit var ctx: ApplicationContext

}
