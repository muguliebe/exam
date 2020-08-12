package com.exam.bank.task

import com.exam.bank.repo.mybatis.TmpMapper
import com.exam.fwk.core.base.BaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class TmpTaskService : BaseService() {

    @Autowired lateinit var mapperTmp: TmpMapper

    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun getRandomString(length: Int): String = (1..length)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

    @Scheduled(fixedRate = 10000)
    fun checkDbLive() {
        val result = mapperTmp.insertTmp(getRandomString(10))
        log.info("db check ok:$result")
    }

}
