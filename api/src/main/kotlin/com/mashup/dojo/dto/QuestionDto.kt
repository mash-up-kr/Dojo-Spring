package com.mashup.dojo.dto

import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.QuestionCategory
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionSetId
import com.mashup.dojo.domain.QuestionSheetId
import com.mashup.dojo.domain.QuestionType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Schema(description = "질문 등록 요청")
data class QuestionCreateRequest(
    @field:NotBlank
    val content: String,
    @field:NotNull
    val type: QuestionType,
    @field:NotNull
    val category: QuestionCategory,
    @field:NotBlank
    @Schema(description = "질문 이모지 이미지 id")
    val emojiImageId: ImageId,
)

@Schema(description = "질문 등록 bulk 요청")
data class QuestionBulkCreateRequest(
    @Schema(description = "질문 요청 list")
    val questionList: List<QuestionCreateRequest>,
)

@Schema(description = "커스텀 질문지 세트 생성 요청")
data class QuestionSetCustomCreateRequest(
    @Schema(description = "질문지 세트를 구성할 질문 id List")
    @field:Size(min = 12, max = 12, message = "질문지 세트는 반드시 12개의 질문으로 구성되어야 합니다.")
    val questionIds: List<QuestionId>,
    @Schema(description = "질문지 세트를 발행할 시각")
    val publishedAt: LocalDateTime,
)


@Schema(description = "질문지 (투표 용지) 한 다스 조회 응답")
data class QuestionSheetsGetResponse(
    @Schema(description = "응답자 id")
    val resolverId: MemberId,
    @Schema(description = "질문지 한 묶음의 고유 식별 값")
    val questionSetId: QuestionSetId,
    @Schema(description = "한 묶음에 해당하는 전체 질문지 수. 1based")
    val sheetTotalCount: Int,
    @Schema(description = "유저가 풀어야하는 질문지 순서(1based). 기존에 푼 적이 없다면 1부터 ~ sheetTotalCount 까지")
    val startingQuestionIndex: Int,
    @Schema(description = "질문지 상세 리스트")
    val questionList: List<QuestionSheetResponse>
)

@Schema(description = "질문지 상세")
data class QuestionSheetResponse(
    @Schema(description = "질문지에 대한 고유 식별 값")
    val questionSheetId: QuestionSheetId,
    @Schema(description = "질문지에 해당하는 응답자 id")
    val resolverId: MemberId,
    @Schema(description = "질문 고유 id")
    val questionId: QuestionId,
    @Schema(description = "질문지 묶음 중 해당 질문지의 순서")
    val questionOrder: Int,
    @Schema(description = "질문 내용")
    val questionContent: String,
    @Schema(description = "질문 카테고리")
    val questionCategory: String,
    @Schema(description = "질문 카테고리에 맞는 이모지 이미지")
    val questionEmojiImage : ByteArray,
    @Schema(description = "질문에 대한 후보자 list")
    val candidates: List<QuestionSheetCandidate>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QuestionSheetResponse

        if (questionSheetId != other.questionSheetId) return false
        if (resolverId != other.resolverId) return false
        if (questionId != other.questionId) return false
        if (questionOrder != other.questionOrder) return false

        return true
    }

    override fun hashCode(): Int {
        var result = questionSheetId.hashCode()
        result = 31 * result + resolverId.hashCode()
        result = 31 * result + questionId.hashCode()
        result = 31 * result + questionOrder
        return result
    }
}
@Schema(description = "질문에 대한 후보자")
data class QuestionSheetCandidate(
    @Schema(description = "후보자 id")
    val memberId: MemberId,
    @Schema(description = "후보자 리스트 중 해당 후보자의 순서")
    val order: Int,
    @Schema(description = "후보자 이름")
    val memberName: String,
    @Schema(description = "후보자 플랫폼")
    val platform: String,
)
