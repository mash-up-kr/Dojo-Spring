package com.mashup.dojo.dto

import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.QuestionId
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
