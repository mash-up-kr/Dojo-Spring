package com.mashup.dojo.dto

import com.mashup.dojo.domain.CoinId
import com.mashup.dojo.domain.MemberId
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Schema(description = "현재 코인 정보 조회")
data class CurrentCoinResponse(
    @Schema(description = "유저 id")
    @field:NotBlank
    val memberId: MemberId,
    @Schema(description = "유저의 코인 id")
    @field:NotBlank
    val coinId: CoinId,
    @Schema(description = "현재 유저의 보유 코인", example = "200L")
    val amount: Long,
    @Schema(description = "마지막으로 코인 정보가 변경된 시각")
    val lastUpdatedAt: LocalDateTime,
)
