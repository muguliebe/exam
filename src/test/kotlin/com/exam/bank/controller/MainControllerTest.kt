package com.exam.bank.controller

import com.exam.bank.base.BaseSpringTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class MainControllerTest :BaseSpringTest() {

    @Test
    fun ping() {
        // when
        val res = rest.getForEntity("/", String::class.java)

        // assert
        assertThat(res.statusCode, equalTo(HttpStatus.OK))
        assertThat(res.body, notNullValue())
        assertThat(res.body, equalTo("pong"))
    }
}
