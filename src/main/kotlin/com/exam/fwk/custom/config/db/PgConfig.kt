package com.exam.fwk.custom.config.db

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class PgConfig {

    @Bean(name = ["embeddedDatasource"])
    @Primary
    fun memoryPg(): DataSource {
        val ds = EmbeddedPostgres.builder()
                .setServerConfig("timezone", "Asia/Seoul")
                .setPort(62501)
                .start().postgresDatabase
        return ds
    }

}
