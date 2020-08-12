package com.exam.bank.repo.mybatis

import com.exam.bank.dto.TransactionHst
import org.springframework.stereotype.Component

@Component
interface TransactionMapper {
    fun selectAllTransaction(): List<TransactionHst>
    fun insertTransaction(transaction: TransactionHst)
}
