package com.vivek.studyapp.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long?.fromMilliToDateString(): String {
    val date: LocalDate =
        this?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
            ?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
}

fun Int.pad(): String {
    return this.toString().padStart(length = 2, padChar = '0')
}


fun Long.fromMilliToHour(): Double {
    return this.let { milli ->
        String.format(Locale.getDefault(), "%.2f", milli.toDouble() / (60 * 60 * 1000))
            .toDouble() //1sec = 1000 milli, 1min = 60sec, 1hr = 60min
    }
}
