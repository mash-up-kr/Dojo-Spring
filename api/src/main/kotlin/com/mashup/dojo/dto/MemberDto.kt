package com.mashup.dojo.dto

import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.MemberGender
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
    @field:NotBlank
    val ordinal: Int, // 기수
    @field:NotBlank
    val gender: MemberGender,
)
