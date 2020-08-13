package com.exam.bank.repo.mybatis

import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface EtrMapper {

    fun selectOne(@Param("userId") userId: Int): String?
    fun insert(@Param("userId") userId: Int, @Param("acctStgCd") stgCd: String)
    fun update(@Param("userId") userId: Int, @Param("acctStgCd") stgCd: String)

}
