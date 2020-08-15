package com.exam.bank.repo.mybatis

import com.exam.bank.dto.FwkTransactionHst
import com.exam.bank.dto.GetTrAuthStatOut
import org.springframework.stereotype.Repository

@Repository
interface TransactionMapper {
    fun selectListAllTransaction(): List<FwkTransactionHst>
    fun insertTransaction(transaction: FwkTransactionHst)
    fun selectTrAuthStat() : List<GetTrAuthStatOut>
}
