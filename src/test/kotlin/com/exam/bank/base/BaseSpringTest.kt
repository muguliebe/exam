package com.exam.bank.base

import com.exam.AppMain
import com.exam.bank.service.AuthService
import org.apache.http.Header
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicHeader
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.util.DefaultUriBuilderFactory

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [AppMain::class])
class BaseSpringTest : BaseTest() {

    @Autowired lateinit var ctx: ApplicationContext
    @Autowired lateinit var serviceAuth: AuthService
    @Autowired lateinit var rest: TestRestTemplate

    // URL 호출을 위한 entry point
    val protocol: String = "http://"
    val host: String = "localhost"
    @LocalServerPort var port: Int = 0

    // URL
    lateinit var basicUri: String
    lateinit var token: String

    @BeforeEach
    fun before() {
        // SpringBootTest 로 구동된 어플리케이션의 주소 및 포트를 확인 합니다.
        basicUri = "$protocol$host:$port"
        log.info("basicUri = $basicUri")

        // TestRestTemplate 에 사용할 토큰을 발행 합니다.
        token = serviceAuth.signIn("1@a.com").jwt

        // TestRestTemplate 에 Authorization 헤더가 디폴트로 지정 되도록 합니다.
        val listHeader = ArrayList<Header>()
        listHeader.add(BasicHeader("Authorization", token))
        val client = HttpClients.custom().setDefaultHeaders(listHeader).build()
        rest.restTemplate.requestFactory = HttpComponentsClientHttpRequestFactory(client)
        rest.restTemplate.uriTemplateHandler = DefaultUriBuilderFactory(basicUri)

    }

}
