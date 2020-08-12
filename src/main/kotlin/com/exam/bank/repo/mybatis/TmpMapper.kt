package com.exam.bank.repo.mybatis

import com.exam.bank.dto.GetTmpListOut
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

@Repository
interface TmpMapper {

    fun selectListTmp(): List<GetTmpListOut>

    @Select("insert into tmp(seq, name) values (default, #{name}) returning seq")
    fun insertTmp(name: String): Int

}
