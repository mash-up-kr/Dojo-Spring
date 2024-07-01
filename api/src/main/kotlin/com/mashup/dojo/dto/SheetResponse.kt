package com.mashup.dojo.dto

import com.mashup.dojo.domain.Candidate
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "질문지 시트 list 조회 응답 (회차별 선정된 질문들)")
data class SheetListResponse(
    @Schema(description = "질문지 시트 리스트. (as-is : 질문 개수 12개")
    val questionSheetResponses: List<SheetResponse>,
)

@Schema(description = "질문지 시트 조회 응답")
data class SheetResponse(
    @Schema(description = "질문지 시트 내 전체 질문 중 현재 질문 순서")
    val currentQuestionIndex: Int,
    @Schema(description = "질문지 시트 내 전체 질문 수")
    val totalIndex: Int,
    val questionResponse: QuestionResponse,
    @Schema(description = "질문에 대한 후보자(shuffle 을 위해 8명 순서 고정되게 제공)")
    val candidates: List<Candidate>,
)

@Schema(description = "질문 정보")
data class QuestionResponse(
    val id: String,
    val content: String,
    val imageUrl: String,
    val sheetId: String,
)
