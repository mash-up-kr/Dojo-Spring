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
    @Scheduled(cron = SCHEDULED_SET_CRON)
    @Async("questionSetSchedulerExecutor")
    fun createQuestionSet() {
        log.info { "=== Start Create questionSet at ${LocalDateTime.now()}. ===" }
        questionUseCase.createQuestionSet()
        log.info { "=== Done Create questionSet at ${LocalDateTime.now()}. ===" }
    }

    @Scheduled(cron = SCHEDULED_SHEET_CRON)
    @Async("questionSheetSchedulerExecutor")
    fun createQuestionSheet() {
        log.info { "=== Start Create questionSheet at ${LocalDateTime.now()}. ===" }
        questionUseCase.createQuestionSheet()
        log.info { "=== Done Create questionSheet at ${LocalDateTime.now()}. ===" }
    }

    companion object {
        private const val SCHEDULED_SET_CRON = "0 0 9,21 * * *" // 매일 9시와 21시에 실행
        private const val SCHEDULED_SHEET_CRON = "0 5 9,21 * * *" // 매일 9시 5분과 21시 5분에 실행
    }
}
