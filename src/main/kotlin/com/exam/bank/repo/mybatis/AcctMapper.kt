package com.exam.bank.repo.mybatis

import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface AcctMapper {

    fun selectOneAcctStg(@Param("userId") userId: Int): String?

}
