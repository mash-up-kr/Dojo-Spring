package com.mashup.dojo.domain

import java.time.LocalDateTime

@JvmInline
value class QuestionSetId(val value: Long)

data class QuestionSet(
    val id: QuestionSetId,
    val questionIds: List<QuestionId>,
    // 질문 발행일
    val publishedAt: LocalDateTime,
)
