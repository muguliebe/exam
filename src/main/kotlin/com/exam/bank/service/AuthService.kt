package com.exam.bank.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.exam.bank.repo.mybatis.UserMapper
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.core.error.BizException
import com.exam.fwk.custom.pojo.ComUser
import com.exam.fwk.custom.util.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.UnsupportedEncodingException

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
        val selectedUser = mapperUser.selectOneUserByEmail(email)

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
                .withClaim("userId", resultUser.userId)
                .withIssuedAt(DateUtils.nowDate())
                .withExpiresAt(DateUtils.fromLocalDateTimeToDate(DateUtils.now()
                        .plusDays(1000))) // 1000일간 유효 for tester
                .sign(algorithm)

        resultUser.jwt = jwt

        // End --------------------------------------------------------------------------------------------------------
        return resultUser

    }

    /**
     * 토큰 디코딩
     */
    fun decodeToken(jwt: String): ComUser? {
        if (!isValidToken(jwt))  // IF 토큰이 유효하지 않다면
            return null          //    THEN null

        val decoded = verifier.verify(jwt)
        val userId = decoded.getClaim("userId").asString().toInt()
        val email = decoded.getClaim("email").asString()

        return ComUser(
                userId = userId
                , email = email
                , jwt = jwt
        )
    }

    /**
     * 유효한 토큰인가?
     */
    fun isValidToken(jwt: String?): Boolean {
        return try {
            if (jwt == null) return false
            verifier.verify(jwt)
            true
        } catch (e: JWTDecodeException) {
            false
        } catch (e: Exception) {
            false
        }
    }

}

