package com.mashup.dojo.domain

import com.mashup.dojo.UUIDGenerator
import java.time.LocalDateTime

@JvmInline
value class CoinId(val value: String)

data class Coin(
    val id: CoinId,
    val memberId: MemberId,
    val amount: Long,
    val lastUpdatedAt: LocalDateTime,
) {
    fun earnCoin(earnedCost: Long): Coin {
        return this.copy(amount = this.amount + earnedCost, lastUpdatedAt = LocalDateTime.now())
    }

    fun useCoin(usedCost: Long): Coin {
        if (this.amount < usedCost) throw IllegalArgumentException("사용할 수 있는 코인이 부족합니다.")
        return this.copy(amount = this.amount - usedCost, lastUpdatedAt = LocalDateTime.now())
    }
}

@JvmInline
value class CoinUseDetailId(val value: String)

data class CoinUseDetail(
    val id: CoinUseDetailId,
    val coinId: CoinId,
    val useType: CoinUseType,
    val reason: String,
    val createdAt: LocalDateTime,
) {
    fun createEarnedCoinUseDetail(
        coinId: CoinId,
        reason: String,
    ): CoinUseDetail {
        return CoinUseDetail(
            id = CoinUseDetailId(UUIDGenerator.generate()),
            coinId = coinId,
            useType = CoinUseType.EARNED,
            reason = reason,
            createdAt = LocalDateTime.now()
        )
    }

    fun createUsedCoinUseDetail(
        coinId: CoinId,
        reason: String,
    ): CoinUseDetail {
        return CoinUseDetail(
            id = CoinUseDetailId(UUIDGenerator.generate()),
            coinId = coinId,
            useType = CoinUseType.USED,
            reason = reason,
            createdAt = LocalDateTime.now()
        )
    }
}

enum class CoinUseType {
    USED,
    EARNED,
}
