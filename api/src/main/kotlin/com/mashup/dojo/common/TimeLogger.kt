package com.mashup.dojo.common

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class TimeLogger {
    private val logger: Logger = LoggerFactory.getLogger(TimeLogger::class.java)

    @PostConstruct
    fun logCurrentTime() {
        val now = ZonedDateTime.now()
        val formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"))
        logger.info("Application started at: $formattedTime")
    }
}
