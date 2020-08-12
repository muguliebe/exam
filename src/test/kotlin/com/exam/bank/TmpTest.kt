package com.exam.bank

import com.exam.bank.controller.TmpController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TmpTest {

    @Autowired lateinit var controllerTmp: TmpController

    @Test
    fun tmpTest() {
    }
}
