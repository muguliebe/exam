package com.exam.bank.repo.mybatis

import com.exam.bank.entity.ComUserMst
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface UserMapper {
    fun selectOneUserById(@Param("userId") userId: Int): ComUserMst?
    fun selectOneUserByEmail(@Param("email") email: String): ComUserMst?
    fun selectListAllUser(): MutableCollection<ComUserMst>
}
