package com.mashup.dojo.dto

import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.PickSort
import com.mashup.dojo.domain.QuestionId
import java.time.LocalDateTime

data class CreatePickRequest(
    val questionId: QuestionId,
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
