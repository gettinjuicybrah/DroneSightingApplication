package com.example.project.service

import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun getRelativeTime(postDateString: String): String {
    return try {
        // Adjust the pattern according to your postDate format
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(Locale.getDefault())
        val postDate = ZonedDateTime.parse(postDateString, formatter)
        val now = ZonedDateTime.now(postDate.zone)
        val duration = Duration.between(postDate, now)

        val minutes = duration.toMinutes()
        val hours = duration.toHours()
        val days = duration.toDays()

        when {
            minutes < 60 -> "$minutes minute${if (minutes != 1L) "s" else ""} ago"
            minutes < 120 -> "1 hour ago"
            hours < 24 -> "$hours hours ago"
            hours < 48 -> "1 day ago"
            else -> "$days days ago"
        }
    } catch (e: Exception) {
        "Unknown time"
    }
}