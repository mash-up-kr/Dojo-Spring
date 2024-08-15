package com.mashup.dojo.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "친구 응답 Response")
data class FriendInfoResponse(
    @Schema(description = "유저 id")
    val memberId: String,
    @Schema(description = "유저 프로필 이미지 url")
    val profileImageUrl: String,
    @Schema(description = "유저 이름")
    val memberName: String,
    @Schema(description = "유저 플랫폼")
    val platform: String,
    @Schema(description = "유저 기수")
    val ordinal: Int,
)
