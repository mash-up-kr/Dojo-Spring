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

    companion object {
        const val INIT_AMOUNT = 200L

        fun create(memberId: MemberId): Coin {
            return Coin(
                id = CoinId(UUIDGenerator.generate()),
                memberId = memberId,
                amount = INIT_AMOUNT,
                lastUpdatedAt = LocalDateTime.now()
            )
        }
    }
}

@JvmInline
value class CoinUseDetailId(val value: String)

data class CoinUseDetail(
    val id: CoinUseDetailId,
    val coinId: CoinId,
    val useType: CoinUseType,
    val cost: Long,
    val detail: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        const val REASON_PROVIDE_DEFAULT = "provide by default"
        const val REASON_USED_FOR_OPEN_PICK = "used for open pick"
        const val REASON_COMPLETE_PICK = "complete pick of QuestionSet"

        fun createEarnedCoinUseDetail(
            coinId: CoinId,
            cost: Long,
            detail: String,
        ): CoinUseDetail {
            return CoinUseDetail(
                id = CoinUseDetailId(UUIDGenerator.generate()),
                coinId = coinId,
                useType = CoinUseType.EARNED,
                cost = cost,
                detail = detail,
                createdAt = LocalDateTime.now()
            )
        }

        fun createUsedCoinUseDetail(
            coinId: CoinId,
            cost: Long,
            detail: String,
        ): CoinUseDetail {
            return CoinUseDetail(
                id = CoinUseDetailId(UUIDGenerator.generate()),
                coinId = coinId,
                useType = CoinUseType.USED,
                cost = cost,
                detail = detail,
                createdAt = LocalDateTime.now()
            )
        }
    }
}

enum class CoinUseType {
    USED,
    EARNED,
}
