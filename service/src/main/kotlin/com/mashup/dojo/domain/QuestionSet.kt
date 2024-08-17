package com.mashup.dojo.domain

import com.mashup.dojo.UUIDGenerator
import java.time.LocalDateTime

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
    fun updateToReady() = this.copy(status = PublishStatus.READY)

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
    READY, // QSheet 생성까지 준비 완료
    UPCOMING, // 예정
}
