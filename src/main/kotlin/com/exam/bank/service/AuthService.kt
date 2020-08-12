package com.exam.bank.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.exam.bank.repo.mybatis.UserMapper
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.core.error.BizException
import com.exam.fwk.custom.pojo.ComUser
import com.exam.fwk.custom.util.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.UnsupportedEncodingException
import javax.annotation.PostConstruct

/**
 * 인증 서비스
 */
@Service
class AuthService : BaseService() {

    private final var algorithm: Algorithm
    private final var verifier: JWTVerifier
    @Autowired lateinit var mapperUser: UserMapper

    init {
        try {
            this.algorithm = Algorithm.HMAC256("exam")
            this.verifier = JWT.require(algorithm)
                    .build()
        } catch (e: UnsupportedEncodingException) {
            log.error("sign err: encode not supported")
            throw Error(e)
        }
    }

    /**
     * 로그인
     */
    fun signIn(email: String): ComUser {

        // Init --------------------------------------------------------------------------------------------------------
        val resultUser = ComUser()
        val selectedUser = mapperUser.selectOneUserId(email)

        // Valid -------------------------------------------------------------------------------------------------------
        if (selectedUser == null) {
            throw BizException("사용자가 존재하지 않습니다.")
        }

        resultUser.userId = selectedUser.userId
        resultUser.email = selectedUser.email

        // Main --------------------------------------------------------------------------------------------------------
        val jwt = JWT.create()
                .withIssuer("com.exam")
                .withClaim("email", email)
                .withIssuedAt(DateUtils.nowDate())
                .withExpiresAt(DateUtils.fromLocalDateTimeToDate(DateUtils.now().plusDays(2))) // 2일간 유효
                .sign(algorithm)

        resultUser.jwt = jwt

        // End --------------------------------------------------------------------------------------------------------
        return resultUser

    }

}
