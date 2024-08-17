package com.mashup.dojo.scheduler

import com.mashup.dojo.usecase.QuestionUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Component
class Scheduler(
    private val questionUseCase: QuestionUseCase,
) {
    @Scheduled(cron = "\${scheduler.cron}")
    @SchedulerLock(name = "createQuestionSet", lockAtMostFor = "PT5M", lockAtLeastFor = "PT4M")
    fun createQuestionSet() {
        log.info { "=== Start Create questionSet at ${LocalDateTime.now()}. ===" }
        questionUseCase.createQuestionSet()
        log.info { "=== Done Create questionSet at ${LocalDateTime.now()}. ===" }
    }

    @Scheduled(cron = "\${scheduler.sheet-cron}")
    @SchedulerLock(name = "createQuestionSheet", lockAtMostFor = "PT5M", lockAtLeastFor = "PT4M")
    fun createQuestionSheet() {
        log.info { "=== Start Create questionSheet at ${LocalDateTime.now()}. ===" }
        questionUseCase.createQuestionSheet()
        log.info { "=== Done Create questionSheet at ${LocalDateTime.now()}. ===" }
    }
}
