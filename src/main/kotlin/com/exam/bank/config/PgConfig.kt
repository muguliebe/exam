package com.exam.bank.config

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class PgConfig {

    @Bean(name= ["firstDs"])
    @Primary
    fun memoryPg(): DataSource = EmbeddedPostgres.builder().start().postgresDatabase

}
