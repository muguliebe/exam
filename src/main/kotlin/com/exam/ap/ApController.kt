package com.exam.ap

import ch.qos.logback.classic.Logger
import com.exam.fwk.custom.util.StringUtils
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

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
    fun getAcctInfo(@RequestParam acctNo: String, @RequestParam bankCd: String): String {
        Thread.sleep(2000)
        return """
            {"name": "${StringUtils.getRandomString(3)}"}
        """.trimIndent()
    }

}
