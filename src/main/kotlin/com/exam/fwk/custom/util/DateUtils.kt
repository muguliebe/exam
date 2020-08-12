package com.exam.fwk.custom.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtils {

    private val zoneId: ZoneId = ZoneId.of("Asia/Seoul")

    private fun now(): LocalDateTime = LocalDateTime.ofInstant(Instant.now(), zoneId)

    // 현재 일시를 지정한 포맷으로 리턴
    fun currentDateTimeFormat(format: String): String = now().format(DateTimeFormatter.ofPattern(format))

    // 현재 일자 (yyyy-MM-dd) 리턴
    fun currentDate(): String = currentDateTimeFormat("yyyy-MM-dd")

    // TimeStamp(yyyy.MM.dd HH:mm:ss) 리턴
    fun currentTimeStamp(): OffsetDateTime = OffsetDateTime.now(zoneId)
    fun currentTimestampString(): String = currentDateTimeFormat("yyyy-MM-dd HH:mm:ss")

}
