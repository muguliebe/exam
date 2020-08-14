package com.exam.bank.repo.mybatis

import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface AcctMapper {

    fun selectOneAcctStg(@Param("userId") userId: Int): String?
    fun selectForUpdate(@Param("userId") userId: Int): String?
    fun insertAcctStg(@Param("userId") userId: Int, @Param("acctStgCd") stgCd: String)
    fun updateAcctStg(@Param("userId") userId: Int, @Param("acctStgCd") stgCd: String)
    fun updateEtcCtn(@Param("userId") userId: Int, @Param("etcCtn") etcCtn: String)

}
