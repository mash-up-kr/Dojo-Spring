package com.mashup.dojo.usecase

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.Coin
import com.mashup.dojo.domain.CoinUseDetail
import com.mashup.dojo.domain.CoinUseDetailId
import com.mashup.dojo.domain.CoinUseType
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.service.CoinService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

interface CoinUseCase {
    data class GetCurrentCoinCommand(val memberId: MemberId)

    data class EarnCoinCommand(val memberId: MemberId, val coinAmount: Long)

    fun getCurrentCoin(command: GetCurrentCoinCommand): Coin

    fun earnCoin(command: EarnCoinCommand): CoinUseDetailId
}

@Component
class DefaultCoinUseCase(
    private val coinService: CoinService,
) : CoinUseCase {
    override fun getCurrentCoin(command: CoinUseCase.GetCurrentCoinCommand): Coin {
        return coinService.getCoin(command.memberId) ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "유저의 코인정보가 없습니다")
    }

    @Transactional
    override fun earnCoin(command: CoinUseCase.EarnCoinCommand): CoinUseDetailId {
        val coin = coinService.getCoin(command.memberId) ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "유저의 코인정보가 없습니다")

        val updatedCoin = coin.earnCoin(command.coinAmount)
        return coinService.updateCoin(CoinUseType.EARNED, CoinUseDetail.REASON_COMPLETE_PICK, command.coinAmount.toInt(), updatedCoin)
    }
}
