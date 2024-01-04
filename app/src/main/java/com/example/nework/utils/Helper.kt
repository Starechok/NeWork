package com.example.nework.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.round

object Helper {
    @JvmStatic
    fun getShortCountView(count: Long): String {
        if (count > 999999) {
            return if (count % 1000000 < 100000)
                (count / 1000000).toString() + "M"
            else (round(count.toDouble() / 1000000 * 10) / 10).toString() + "M"
        }
        if (count > 9999) return (count / 1000).toString() + "K"
        if (count > 999) {
            return if (count % 1000 < 100)
                (count / 1000).toString() + "K"
            else (round(count.toDouble() / 1000 * 10) / 10).toString() + "K"
        }
        return count.toString()
    }

    @JvmStatic
    fun getDate(date: String): String {
        val localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        return localDateTime.format(DateTimeFormatter.ISO_DATE)
    }

    @JvmStatic
    fun getTime(time: String): String {
        val localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME)
        return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    @JvmStatic
    fun getDatetime(date: String, time: String): String {
        val parsedDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
        val parsedTime = LocalTime.parse(time, DateTimeFormatter.ISO_TIME)
        return LocalDateTime.of(parsedDate, parsedTime).format(DateTimeFormatter.ISO_DATE_TIME)
    }
}