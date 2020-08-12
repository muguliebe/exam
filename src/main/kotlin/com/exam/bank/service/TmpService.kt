package com.exam.bank.service

import com.exam.bank.dto.GetTmpListOut
import com.exam.bank.dto.TransactionHst
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

    fun getTmpList(): List<GetTmpListOut> {
        val result = mapperTmp.selectListTmp()
        log.info("what:$result")
        return result
    }

    fun getTrList(): List<TransactionHst> = mapperTr.selectListAllTransaction()

    fun getError() {
        throw BizException("sad")
    }

}
