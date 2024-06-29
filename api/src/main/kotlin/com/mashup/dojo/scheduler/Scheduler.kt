package com.mashup.dojo.scheduler

import com.mashup.dojo.usecase.QuestionUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Component
class Scheduler(
    private val questionUseCase: QuestionUseCase,
) {
    @Scheduled(cron = SCHEDULED_CRON)
    @Async("questionSetSchedulerExecutor")
    fun createQuestionSet() {
        log.info { "=== Start Create questionSet at ${LocalDateTime.now()}. ===" }
        questionUseCase.createQuestionSet()
        log.info { "=== Done Create questionSet at ${LocalDateTime.now()}. ===" }
    }

    companion object {
        private const val SCHEDULED_CRON = "0 0 9,21 * * *"
    }
}
