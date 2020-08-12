package com.exam.bank.repo.mybatis

import com.exam.bank.dto.ComUserMst
import org.springframework.stereotype.Repository

@Repository
interface UserMapper {
    fun selectOneUserId(email: String): ComUserMst?
    fun selectListAllUser(): MutableCollection<ComUserMst>
}
