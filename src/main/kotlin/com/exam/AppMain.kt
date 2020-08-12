package com.exam

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
        scanBasePackages = ["com.exam"]
)
class AppMain

fun main(args: Array<String>) {
    runApplication<AppMain>(*args)
}
