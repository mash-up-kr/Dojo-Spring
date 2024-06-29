package com.mashup.dojo.dto

import com.mashup.dojo.domain.QuestionType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "질문 등록 요청")
data class QuestionCreateRequest(
    @field:NotBlank
    val content: String,
    @field:NotNull
    val type: QuestionType,
    @field:NotBlank
    @Schema(description = "질문 이미지")
    val imageUrl: String,
)

@Schema(description = "질문 등록 bulk 요청")
data class QuestionBulkCreateRequest(
    @Schema(description = "질문 요청 list")
    val questionList: List<QuestionCreateRequest>,
)
