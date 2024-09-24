package com.mashup.dojo.dto

import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.MemberGender
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.MemberPlatform
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "멤버 등록 요청")
data class MemberCreateRequest(
    @field:NotBlank
    val fullName: String,
    val profileImageId: ImageId?,
    @field:NotBlank
    val platform: MemberPlatform,
    // 기수
    @field:NotBlank
    val ordinal: Int,
    @field:NotBlank
    val gender: MemberGender,
)

@Schema(description = "멤버 정보 수정 요청 / 수정이 필요한 요소만 not-null 값으로 받아요")
data class MemberUpdateRequest(
    val profileImageId: ImageId?,
)

@Schema(description = "팔로우 생성 요청")
data class MemberCreateFriendRelationRequest(
    @field:NotBlank
    @Schema(description = "팔로우 요청한 유저 id")
    val fromMemberId: MemberId,
    @field:NotBlank
    @Schema(description = "팔로우 대상 유저 id")
    val toMemberId: MemberId,
)

@Schema(description = "팔로우 해제 요청")
data class MemberDeleteFriendRelationRequest(
    @field:NotBlank
    @Schema(description = "팔로우 해제 대상 유저 id")
    val toMemberId: MemberId,
)
