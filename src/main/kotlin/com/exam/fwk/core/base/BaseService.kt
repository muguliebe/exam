package com.exam.fwk.core.base

import ch.qos.logback.classic.Logger
import com.exam.fwk.core.component.Area
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.request.RequestContextHolder

@Transactional
abstract class BaseService : BaseObject() {

    protected final val log = LoggerFactory.getLogger(this::class.java) as Logger

    @Autowired lateinit var areaReal: Area

    var area = Area()
        get() = if (RequestContextHolder.getRequestAttributes() != null) {
            areaReal
        } else {
            field
        }

}
