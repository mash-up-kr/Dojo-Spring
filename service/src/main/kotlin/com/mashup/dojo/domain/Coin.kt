package com.mashup.dojo.domain

import java.time.LocalDateTime

@JvmInline
value class CoinId(val value: String)

data class Coin(
    val id: CoinId,
    val memberId: MemberId,
    val amount: Long,
    val lastUpdatedAt: LocalDateTime,
)

@JvmInline
value class CoinUseDetailId(val value: String)

data class CoinUseDetail(
    val id: CoinUseDetailId,
    val coinId: CoinId,
    val useType: CoinUseType,
    val reason: String,
    val createdAt: LocalDateTime,
)

enum class CoinUseType {
    USED,
    EARNED,
}
