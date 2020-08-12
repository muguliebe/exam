package com.exam.bank.service

import com.exam.bank.repo.mybatis.AcctMapper
import com.exam.fwk.core.base.BaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * 계좌 서비스
 */
@Service
class AcctService : BaseService() {

    @Autowired lateinit var mapperAcct: AcctMapper

    /**
     * 계좌 인증 단계 코드 조회
     * - 해당 사용자의 단계가 없다면 A0 리턴
     */
    fun getAcctStgCd(userId: Int) = mapperAcct.selectOneAcctStg(userId) ?: "A0"

}
