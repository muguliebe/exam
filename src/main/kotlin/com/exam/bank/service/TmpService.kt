package com.exam.bank.service

import com.exam.bank.dto.GetTmpListOut
import com.exam.bank.dto.FwkTransactionHst
import com.exam.bank.repo.jpa.UserRepo
import com.exam.bank.repo.mybatis.TmpMapper
import com.exam.bank.repo.mybatis.TransactionMapper
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.core.error.BizException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


/**
 * 구조 테스트용 서비스
 */
@Service
class TmpService : BaseService() {

    @Autowired lateinit var mapperTmp: TmpMapper
    @Autowired lateinit var mapperTr: TransactionMapper
    @Autowired lateinit var repoUser: UserRepo

    fun getTmpList(): List<GetTmpListOut> {
        val result = mapperTmp.selectListTmp()
        log.info("what:$result")
        return result
    }

    fun getTrList(): List<FwkTransactionHst> = mapperTr.selectListAllTransaction()

    fun getError() {
        throw BizException("sad")
    }

    fun testJpa() = repoUser.findAll()

    fun comment() {
        // Init --------------------------------------------------------------------------------------------------------
        // Main --------------------------------------------------------------------------------------------------------
        // End ---------------------------------------------------------------------------------------------------------
    }

}
