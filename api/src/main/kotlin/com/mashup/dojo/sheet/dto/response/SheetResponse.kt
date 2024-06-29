package com.mashup.dojo.sheet.dto.response

data class SheetResponse(
    val questionSetId: Long,
    val questionId: Long,
    val imageUrl: String,
    val candidates: List<MemberResponse>,
)

data class MemberResponse(
    val memberId: Long,
    val memberName: String,
    val order: Int,
)
