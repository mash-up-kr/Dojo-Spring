package com.mashup.dojo.domain

import java.time.LocalDateTime

@JvmInline
value class QuestionId(val value: Long)

data class Question(
    val id: QuestionId,
    val content: String,
    val type: QuestionType,
    // Todo 정책 확인하고 이름 확실하게 짓기
    val emojiImageId: ImageId,
    val createdAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
)

enum class QuestionType {
    FRIEND,
    ACCOMPANY,
}

@JvmInline
value class QuestionSheetId(val value: Long)

data class QuestionSheet(
    val questionSheetId: QuestionSheetId,
    val questionSetId: QuestionSetId,
    val questionId: QuestionId,
    val resolverId: MemberId,
    // Todo Json String으로 저장하자
    val candidates: List<Candidate>,
)

data class Candidate(
    val memberId: MemberId,
    val order: Int,
)
