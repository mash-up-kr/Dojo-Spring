package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.config.security.MemberPrincipalContextHolder
import com.mashup.dojo.domain.CoinUseDetailId
import com.mashup.dojo.dto.CurrentCoinResponse
import com.mashup.dojo.usecase.CoinUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Coin", description = "코인 API")
@RestController
@RequestMapping("/coin")
class CoinController(
    private val coinUseCase: CoinUseCase,
) {
    @GetMapping
    @Operation(
        summary = "본인의 현재 코인을 조회합니다.",
        description = "유저의 현재 남은 코인 정보를 조회합니다",
        responses = [
            ApiResponse(responseCode = "200", description = "코인 정보 조회")
        ]
    )
    fun getCurrentCoin(): DojoApiResponse<CurrentCoinResponse> {
        val memberId = MemberPrincipalContextHolder.current().id
        val currentCoin = coinUseCase.getCurrentCoin(CoinUseCase.GetCurrentCoinCommand(memberId))

        return DojoApiResponse.success(
            CurrentCoinResponse(
                coinId = currentCoin.id,
                memberId = currentCoin.memberId,
                amount = currentCoin.amount,
                lastUpdatedAt = currentCoin.lastUpdatedAt
            )
        )
    }

    @GetMapping("/current/question-set/solved-picks")
    @Operation(
        summary = "현재 운영중인 QuestionSheet에서 지급된 코인을 반환합니다.",
        description = "현재 운영중인 QuestionSheet에서 지급된 코인 반환 API",
        responses = [
            ApiResponse(responseCode = "200", description = "투표 완료 보상 제공 성공")
        ]
    )
    fun getCoinBySolvedPick(): DojoApiResponse<CoinUseCase.CoinBySolvedPick> {
        val memberId = MemberPrincipalContextHolder.current().id
        return coinUseCase.getCoinBySolvedPickList(memberId).let {
            DojoApiResponse.success(it)
        }
    }

    @PostMapping("/admin/update")
    @Operation(
        summary = "관리자가 직접 특정 사용자에게 잼을 제공하는 API",
        description = "특정 사용자에게 잼을 제공하는 API (관리자 전용)",
        responses = [
            ApiResponse(responseCode = "200", description = "관리자 보상 제공 성공")
        ]
    )
    fun provideCoinByEvent(
        @RequestParam fullName: String,
        @RequestParam amount: Long,
        @RequestParam(required = false) platform: String?,
    ): DojoApiResponse<CoinUseDetailId> {
        val currentMemberId = MemberPrincipalContextHolder.current().id
        return coinUseCase.earnCoinByEvent(
            CoinUseCase.EarnCoinByEventCommand(
                currentMemberId = currentMemberId,
                fullName = fullName,
                platform = platform,
                coinAmount = amount
            )
        ).let { DojoApiResponse.success(it) }
    }
}

private const val DEFAULT_COMPLETE_PICK_OFFER_AMOUNT = 200L
