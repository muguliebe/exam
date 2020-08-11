package com.exam.bank.config

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.sql.DataSource

@Component
class Migrate {

    @Autowired lateinit var ds: DataSource

    @PostConstruct
    fun flyway() {
        val flyway = Flyway.configure()
                .dataSource(ds)
                .locations("db/migration")
                .load()
        flyway.migrate()
    }
}
