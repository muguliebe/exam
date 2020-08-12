package com.exam.fwk.custom.service

import com.exam.bank.dto.TransactionHst
import com.exam.bank.repo.mybatis.TransactionMapper
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.custom.pojo.CommonArea
import com.exam.fwk.custom.util.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class TransactionService : BaseService() {

    @Autowired lateinit var mapper: TransactionMapper  // 거래내역

    fun getTransactions() {
        mapper.selectAllTransaction()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun insertTransaction(commons: CommonArea) {

        // set insert input
        val tr = TransactionHst()
        tr.transactionDate = LocalDate.parse(commons.date)
        tr.gid = commons.gid
        tr.method = commons.method
        tr.path = commons.path
        tr.statCode = commons.statCd
        tr.startTime = commons.startDt?.format(DateTimeFormatter.ofPattern("HHmmss"))
        tr.endTime = commons.endDt?.format(DateTimeFormatter.ofPattern("HHmmss"))
        tr.elapsed = when (commons.elapsed) {
            null -> Duration.between(commons.startDt, commons.endDt).toMillis()
            else -> commons.elapsed
        }
        tr.remoteIp = commons.remoteIp
        tr.queryString = commons.queryString
        tr.body = commons.body
        tr.errMsg = commons.errMsg
        tr.referrer = commons.referrer
        tr.createDt = DateUtils.currentTimeStamp()
        tr.createUserId = commons.user?.userId

        // insert
        mapper.insertTransaction(tr)
    }
}
