package com.exam.bank.repo.mybatis

import com.exam.bank.dto.TestTmpOut
import org.springframework.stereotype.Repository

@Repository
interface TmpMapper {
    fun selectListTmp(): List<TestTmpOut>
}
