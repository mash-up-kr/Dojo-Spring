package com.mashup.dojo.service

import com.mashup.dojo.CoinEntity
import com.mashup.dojo.CoinRepository
import com.mashup.dojo.CoinUseDetailEntity
import com.mashup.dojo.CoinUseDetailRepository
import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.UsageStatus
import com.mashup.dojo.domain.Coin
import com.mashup.dojo.domain.CoinId
import com.mashup.dojo.domain.CoinUseDetail
import com.mashup.dojo.domain.CoinUseDetailId
import com.mashup.dojo.domain.CoinUseType
import com.mashup.dojo.domain.MemberId
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

interface CoinService {
    fun getCoin(memberId: MemberId): Coin?

    fun create(memberId: MemberId): CoinId

    fun rewardCoinForCompletePick(memberId: MemberId): CoinUseDetailId

    fun updateCoin(
        useType: CoinUseType,
        detail: String,
        cost: Int,
        coin: Coin,
    ): CoinUseDetailId
}

@Service
class DefaultCoinService(
    private val coinRepository: CoinRepository,
    private val coinUseDetailRepository: CoinUseDetailRepository,
) : CoinService {
    override fun getCoin(memberId: MemberId): Coin? {
        return coinRepository.findByMemberId(memberId.value)?.toCoin()
    }

    override fun create(memberId: MemberId): CoinId {
        val coin = Coin.create(memberId)
        val coinUseDetail = CoinUseDetail.createEarnedCoinUseDetail(coin.id, Coin.INIT_AMOUNT, CoinUseDetail.REASON_PROVIDE_DEFAULT)

        coinRepository.save(coin.toEntity())
        coinUseDetailRepository.save(coinUseDetail.toEntity())

        return coin.id
    }

    @Transactional
    override fun rewardCoinForCompletePick(memberId: MemberId): CoinUseDetailId {
        val coin = getCoin(memberId) ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "유저의 코인정보가 없습니다")
        val updatedCoin = coin.earnCoin(COMPLETE_PICK_COIN_AMOUNT.toLong())

        return updateCoin(
            useType = CoinUseType.EARNED,
            detail = CoinUseDetail.REASON_COMPLETE_PICK,
            cost = COMPLETE_PICK_COIN_AMOUNT,
            coin = updatedCoin
        )
    }

    override fun updateCoin(
        useType: CoinUseType,
        detail: String,
        cost: Int,
        coin: Coin,
    ): CoinUseDetailId {
        coinRepository.save(coin.toEntity())

        val coinUseDetail =
            if (CoinUseType.USED == useType) {
                CoinUseDetail.createUsedCoinUseDetail(coin.id, cost.toLong(), detail)
            } else {
                CoinUseDetail.createEarnedCoinUseDetail(coin.id, cost.toLong(), detail)
            }

        val coinUseDetailEntity = coinUseDetailRepository.save(coinUseDetail.toEntity())
        return CoinUseDetailId(coinUseDetailEntity.id)
    }

    companion object {
        private const val COMPLETE_PICK_COIN_AMOUNT = 200
    }
}

private fun CoinEntity.toCoin(): Coin {
    return Coin(
        id = CoinId(id),
        memberId = MemberId(memberId),
        amount = amount,
        lastUpdatedAt = updatedAt
    )
}

private fun Coin.toEntity(): CoinEntity {
    return CoinEntity(
        id = id.value,
        memberId = memberId.value,
        amount = amount
    )
}

private fun CoinUseDetail.toEntity(): CoinUseDetailEntity {
    return CoinUseDetailEntity(
        id = id.value,
        coinId = coinId.value,
        usageStatus =
            when (useType) {
                CoinUseType.USED -> UsageStatus.USED
                CoinUseType.EARNED -> UsageStatus.EARNED
            },
        cost = cost,
        detail = detail
    )
}
