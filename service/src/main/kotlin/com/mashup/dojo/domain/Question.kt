package com.mashup.dojo.domain

import java.time.LocalDateTime

@JvmInline
value class QuestionId(val value: String)

data class Question(
    val id: QuestionId,
    val content: String,
    val type: QuestionType,
    val category: QuestionCategory,
    val emojiImageId: ImageId,
    val createdAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
)

enum class QuestionType {
    FRIEND,
    ACCOMPANY,
}

// todo : category 상세 기획에 따라 변경 가능 (현재 임시)
//  카테고리에 따라 emojiImageId 가 결정
enum class QuestionCategory {
    ROMANCE,
    WORK,
    FUN,
    PERSONALITY,
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
