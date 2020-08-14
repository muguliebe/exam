package com.exam.ap

import ch.qos.logback.classic.Logger
import com.exam.fwk.custom.util.StringUtils
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import kotlin.random.Random

/**
 * 외부 AP 로 간주
 */
@RestController
@RequestMapping("/ap")
@Api(value = "/ap", description = "외부 AP")
class ApController {

    val log = LoggerFactory.getLogger(ApController::class.java) as Logger

    @PostMapping("/auth/ic")
    @ApiOperation(value = "신분증 이미지 분석")
    fun authIc(@RequestBody input: ApAuthIcIn): ApAuthIcOut {
        return ApAuthIcOut(
                result = "OK",
                name = StringUtils.getRandomString(3),
                ci = StringUtils.getRandomString(88),
                no = StringUtils.getRandomNumber(13)
        )
    }

    data class ApAuthIcIn(
            val image: String = "" // 이미지가 아닌 스트링으로 대체 합니다.
    )

    data class ApAuthIcOut(
            val result: String, // 결과[정상:OK]
            val ci: String,     // Connecting Information
            val name: String,   // 성명
            val no: String      // 번호(주민 Or 운전)
    )

    @PostMapping("/cln/auth-info")
    @ApiOperation(value = "고객 신분증 정보 Update")
    fun updateClnAuthInfo(@RequestBody input: UpdateClnAuthInfoIn) {
        return
    }

    data class UpdateClnAuthInfoIn(
            val name: String,
            val ci: String
    )

    @GetMapping("/ext/acct")
    @ApiOperation(value = "타행 계좌 정보 조회")
    fun getAcctInfo(@RequestParam acctNo: String, @RequestParam bankCd: String, @RequestParam name: String): String {
        val sleepTime = (1..4)
                .map { Random.nextInt(0, 4) }
                .joinToString("").toLong()

        log.info("[대외 AP] 타행 계좌 정보 조회는 $sleepTime ms 이후에 응답합니다. [$name]")
        Thread.sleep(sleepTime)

        val returnNm = name

        return """
            {"name": "$returnNm"}
        """.trimIndent()
    }

    @PostMapping("/ext/acct")
    @ApiOperation(value = "타행 계좌 1원 이체")
    fun sendExtBankOneWon(@RequestBody input: SendExtBankOneWonIn): String {

        val sleepTime = (1..4)
                .map { Random.nextInt(0, 4) }
                .joinToString("").toLong()

        log.info("[대외 AP] 타행 계좌 이체는 $sleepTime ms 이후에 응답합니다. [$input.etcCtn]")
        Thread.sleep(sleepTime)

        return """
            {
              "acctNo": "${input.acctNo}"
              , "etcCtn": "${input.etcCtn}"
             }
        """.trimIndent()
    }

    data class SendExtBankOneWonIn(
            val acctNo: String,
            val bankCd: String,
            val etcCtn: String
    )

    @PostMapping("/deps/acct")
    @ApiOperation(value = "수신계좌생성")
    fun createDepsAcct(@RequestBody input: CreateDepsAcctIn): String {

        val acctNo = StringUtils.getRandomNumber(13)

        return """
            { "acctNo": "${acctNo}" }
        """.trimIndent()
    }

    data class CreateDepsAcctIn(
            var userId: String = ""
    )


}
