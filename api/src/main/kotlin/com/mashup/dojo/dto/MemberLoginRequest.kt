package com.mashup.dojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "멤버 로그인 요청")
data class MemberLoginRequest(
    @field:NotBlank
    val id: String,
)
