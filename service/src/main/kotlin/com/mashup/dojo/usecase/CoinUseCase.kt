package com.mashup.dojo.usecase

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.Coin
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.service.CoinService
import org.springframework.stereotype.Component

interface CoinUseCase {
    data class GetCurrentCoinCommand(val memberId: MemberId)

    fun getCurrentCoin(command: GetCurrentCoinCommand): Coin
}

@Component
class DefaultCoinUseCase(
    private val coinService: CoinService,
) : CoinUseCase {
    override fun getCurrentCoin(command: CoinUseCase.GetCurrentCoinCommand): Coin {
        return coinService.getCoin(command.memberId) ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "유저의 코인정보가 없습니다")
    }
}
