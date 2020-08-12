package com.exam.bank.service

import com.exam.fwk.core.base.BaseService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ExtService : BaseService() {

    @Value("\${server.port}") var port:Int = 0



}
