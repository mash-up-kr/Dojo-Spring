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
    val publishedYn: Boolean = false,
    // 질문 발행일
    val publishedAt: LocalDateTime,
) {
    companion object {
        fun create(
            questionOrders: List<QuestionOrder>,
            publishedAt: LocalDateTime,
        ): QuestionSet {
            return QuestionSet(
                id = QuestionSetId(UUIDGenerator.generate()),
                questionIds = questionOrders,
                publishedAt = publishedAt
            )
        }
    }
}
