package com.exam.bank.task

import com.exam.bank.repo.mybatis.TmpMapper
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.custom.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

/**
 * schedule 테스트용
 * - 1분 마다 DB 연결 Check
 */
@Service
class TmpTaskService : BaseService() {

    @Autowired lateinit var mapperTmp: TmpMapper


    @Scheduled(fixedRate = 60000)
    fun checkDbLive() {
        val result = mapperTmp.insertTmp(StringUtils.getRandomString(10))
        log.info("db check ok:$result")
    }

}
