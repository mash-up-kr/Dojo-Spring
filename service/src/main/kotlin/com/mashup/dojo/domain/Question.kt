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

enum class QuestionCategory(
    private val value: String,
) {
    DATING("연애"),
    FRIENDSHIP("사교"),
    PERSONALITY("성격"),
    ENTERTAINMENT("유흥"),
    FITNESS("체력"),
    APPEARANCE("외모"),
    WORK("작업"),
    HUMOR("유머"),
    OTHER("기타"),
}

@JvmInline
value class QuestionSheetId(val value: String)

data class QuestionSheet(
    val questionSheetId: QuestionSheetId,
    // 문제지 조회를 위해 필요 !
    val questionSetId: QuestionSetId,
    val questionId: QuestionId,
    val resolverId: MemberId,
    // Todo Json String으로 저장하자
    val candidates: List<Candidate>,
)

data class Candidate(
    val memberId: MemberId,
    val memberName: String,
    val order: Int,
)
