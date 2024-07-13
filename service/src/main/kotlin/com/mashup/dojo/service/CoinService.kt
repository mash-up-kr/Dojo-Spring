package com.mashup.dojo.service

import com.mashup.dojo.domain.Coin
import com.mashup.dojo.domain.CoinId
import com.mashup.dojo.domain.MemberId
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface CoinService {
    fun getCoin(memberId: MemberId): Coin
}

@Service
class DefaultCoinService(
    // TODO :  private val coinRepository: CoinRepository
) : CoinService {
    override fun getCoin(memberId: MemberId): Coin {
        // return coinRepository.findByMemberId(memberId)
        return DEFAULT_COIN_INFO
    }

    companion object {
        val DEFAULT_COIN_INFO =
            Coin(
                id = CoinId("1"),
                memberId = MemberId("1"),
                amount = 200L,
                lastUpdatedAt = LocalDateTime.now()
            )
    }
}
