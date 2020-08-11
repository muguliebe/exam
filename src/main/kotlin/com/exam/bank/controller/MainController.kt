package com.exam.bank.controller

import ch.qos.logback.classic.Logger
import com.exam.bank.dto.TestTmpOut
import com.exam.bank.repo.mybatis.TmpMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.ResultSet


@RestController
class MainController {

    companion object {
        var log: Logger = LoggerFactory.getLogger(MainController::class.java) as Logger
    }

    @Autowired lateinit var jdbcTemplate: JdbcTemplate
    @Autowired lateinit var mapperTmp: TmpMapper

    @GetMapping
    fun ping() = "pong"

    @Scheduled(fixedRate = 10000)
    fun checkDbLive() {
        jdbcTemplate.execute("insert into tmp(name) values ('a')")
        jdbcTemplate.query<Int>("SELECT seq, name FROM tmp order by seq desc") { rs: ResultSet ->
            rs.next()
            val seq = rs.getInt("seq")
            log.info("db insert & select test:$seq")
            seq
        }
    }

    @GetMapping("/test")
    fun testTmpOut(): List<TestTmpOut> {
        val result = mapperTmp.selectListTmp()
        log.info("what:$result")
        return result
    }
}
