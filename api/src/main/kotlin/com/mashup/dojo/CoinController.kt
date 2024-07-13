package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.dto.CurrentCoinResponse
import com.mashup.dojo.usecase.CoinUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
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
    fun getCurrentCoin(): DojoApiResponse<CurrentCoinResponse> { // TODO : param UserInfo
        val currentCoin = coinUseCase.getCurrentCoin(CoinUseCase.GetCurrentCoinCommand(MemberId("1")))

        return DojoApiResponse.success(
            CurrentCoinResponse(
                coinId = currentCoin.id,
                memberId = currentCoin.memberId,
                amount = currentCoin.amount,
                lastUpdatedAt = currentCoin.lastUpdatedAt
            )
        )
    }
}
