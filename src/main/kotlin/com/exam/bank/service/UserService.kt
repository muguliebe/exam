package com.exam.bank.service

import com.exam.bank.repo.mybatis.UserMapper
import com.exam.fwk.core.base.BaseService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class UserService(
        var mapperUser: UserMapper
) : BaseService() {

    /**
     * 최초 구동 후, 모든 사용자 토큰을 로깅하기 위한 용도 for Tester
     */
    @Scheduled(initialDelay = 800, fixedDelay = Long.MAX_VALUE)
    fun logAllUserInfo() {
        val serviceAuth = ctx.getBean(AuthService::class.java)

        getAllUser()
                .map { serviceAuth.signIn(it.email) }
                .forEachIndexed { index, user ->
                    if (index == 0) {
                        log.info("")
                        log.info("========= 모든 사용자 Info Start ======================================================================")
                    }
                    log.info("id[${user.userId}] email[${user.email}] jwt= ${user.jwt}")
                }
        log.info("========= 모든 사용자 Info End   ======================================================================\n")
    }

    /**
     * 모든 사용자 조회
     */
    fun getAllUser() = mapperUser.selectListAllUser()

    /**
     * 사용자 단건 조회 by ID
     */
    fun getUserById(id:Int) = mapperUser.selectOneUserById(id)

}
