package com.exam.bank.repo.mybatis

import com.exam.bank.dto.TransactionHst
import org.springframework.stereotype.Repository

@Repository
interface TransactionMapper {
    fun selectListAllTransaction(): List<TransactionHst>
    fun insertTransaction(transaction: TransactionHst)
}
