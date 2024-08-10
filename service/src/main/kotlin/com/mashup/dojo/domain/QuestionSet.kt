package com.mashup.dojo.domain

import com.mashup.dojo.UUIDGenerator
import java.time.LocalDateTime
import java.time.LocalTime

@JvmInline
value class QuestionSetId(val value: String)

data class QuestionOrder(
    val questionId: QuestionId,
    val order: Int,
)

data class QuestionSet(
    val id: QuestionSetId,
    // 1 based-order
    val questionIds: List<QuestionOrder>,
    // 발행 여부
    val status: PublishStatus = PublishStatus.UPCOMING,
    // 질문 발행일
    val publishedAt: LocalDateTime,
    val endAt: LocalDateTime,
) {
    companion object {
        fun create(
            questionOrders: List<QuestionOrder>,
            publishedAt: LocalDateTime,
            endAt: LocalDateTime,
        ): QuestionSet {
            return QuestionSet(
                id = QuestionSetId(UUIDGenerator.generate()),
                questionIds = questionOrders,
                publishedAt = publishedAt,
                endAt = endAt
            )
        }
    }
}

enum class PublishStatus {
    TERMINATED, // 종료
    ACTIVE, // 운영중
    UPCOMING, // 예정
}

object PublishedTime {
    val OPEN_TIME_1: LocalTime = LocalTime.of(8, 0, 0)
    val OPEN_TIME_2: LocalTime = LocalTime.of(11, 0, 0)
}
