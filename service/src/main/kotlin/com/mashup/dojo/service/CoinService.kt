package com.mashup.dojo.service

import com.mashup.dojo.CoinEntity
import com.mashup.dojo.CoinRepository
import com.mashup.dojo.CoinUseDetailRepository
import com.mashup.dojo.domain.Coin
import com.mashup.dojo.domain.CoinId
import com.mashup.dojo.domain.MemberId
import org.springframework.stereotype.Service

interface CoinService {
    fun getCoin(memberId: MemberId): Coin?
}

@Service
class DefaultCoinService(
    private val coinRepository: CoinRepository,
    private val coinUseDetailRepository: CoinUseDetailRepository,
) : CoinService {
    override fun getCoin(memberId: MemberId): Coin? {
        return coinRepository.findByMemberId(memberId.value)?.toDomain()
    }

    companion object {
        private fun CoinEntity.toDomain(): Coin {
            return Coin(
                id = CoinId(id),
                memberId = MemberId(memberId),
                amount = amount,
                lastUpdatedAt = updatedAt
            )
        }
    }
}
