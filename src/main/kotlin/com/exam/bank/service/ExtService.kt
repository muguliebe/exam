package com.exam.bank.service

import com.exam.bank.repo.mybatis.EtrMapper
import com.exam.fwk.core.base.BaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * 뱅킹 내 대외 서비스
 */
@Service
class ExtService : BaseService() {

    @Autowired lateinit var mapper: EtrMapper

    /**
     * 타행 계좌 조회 등록
     */
    fun regAcctGet() {
    }

}
