package com.exam

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(
        scanBasePackages = ["com.exam"]
        , exclude = [DataSourceAutoConfiguration::class]

)
class AppMain

fun main(args: Array<String>) {
    runApplication<AppMain>(*args)
}
