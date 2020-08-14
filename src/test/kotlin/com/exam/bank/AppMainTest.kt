package com.exam.bank

import com.exam.bank.base.BaseSpringTest
import org.junit.jupiter.api.Test

class AppMainTest : BaseSpringTest() {

    @Test
    fun `부팅이 잘 되는가`() {
        log.info("부팅 끝")
    }

    @Test
    fun `what`(){
        val res = rest.getForEntity("/ping", String::class.java)

    }

}
