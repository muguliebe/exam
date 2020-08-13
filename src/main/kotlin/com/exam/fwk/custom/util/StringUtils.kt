package com.exam.fwk.custom.util

import kotlin.random.Random

object StringUtils {

    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun getRandomString(length: Int): String = (1..length)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

    fun getRandomNumber(length: Int): String = (1..length)
            .map { Random.nextInt(0, 10) }
            .joinToString("")

}
