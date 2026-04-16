package com.solosystem.app.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    private val dateFormatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter  = DateTimeFormatter.ofPattern("HH:mm")

    fun today(): String = LocalDate.now().format(dateFormatter)
    fun now(): String   = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    fun time(): String  = LocalDateTime.now().format(timeFormatter)

    fun isToday(dateStr: String): Boolean =
        runCatching { LocalDate.parse(dateStr, dateFormatter) == LocalDate.now() }.getOrDefault(false)

    fun daysBetween(from: String, to: String): Long =
        runCatching {
            val f = LocalDate.parse(from, dateFormatter)
            val t = LocalDate.parse(to,   dateFormatter)
            java.time.temporal.ChronoUnit.DAYS.between(f, t)
        }.getOrDefault(0L)

    fun streakMultiplier(streak: Int): Double = when {
        streak >= 90 -> 2.0
        streak >= 60 -> 1.75
        streak >= 30 -> 1.5
        streak >= 14 -> 1.2
        streak >= 7  -> 1.1
        else         -> 1.0
    }

    fun xpWithStreak(baseXp: Int, streak: Int): Int =
        (baseXp * streakMultiplier(streak)).toInt()
}