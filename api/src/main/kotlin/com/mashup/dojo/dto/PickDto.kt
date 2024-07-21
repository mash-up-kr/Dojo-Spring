package com.mashup.dojo.dto

import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.PickId
import com.mashup.dojo.domain.PickOpenItem
import com.mashup.dojo.domain.PickSort
import com.mashup.dojo.domain.QuestionId
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Schema(description = "Pick 생성 요청")
data class CreatePickRequest(
    @Schema(description = "질문 id")
    val questionId: QuestionId,
    @Schema(description = "후보자 중 선택한 대상 멤버 id")
    val pickedId: MemberId,
)

data class ReceivedPickListGetResponse(
    val pickList: List<PickResponse>,
    val sort: PickSort,
)

// todo : 질문의 유형(카테고리)도 전달해줘야 하는가
data class PickResponse(
    val questionId: QuestionId,
    val questionContent: String,
    val questionEmojiImageUrl: String,
    val totalReceivedPickCount: Int,
    val latestPickedAt: LocalDateTime,
)

@Schema(description = "픽 오픈 요청")
data class PickOpenRequest(
    @field:NotBlank
    val pickId: PickId,
    @field:NotNull
    val pickOpenItem: PickOpenItem,
)

data class PickOpenResponse(
    val pickId: PickId,
    val pickOpenItem: PickOpenItem,
    val value: String,
)
