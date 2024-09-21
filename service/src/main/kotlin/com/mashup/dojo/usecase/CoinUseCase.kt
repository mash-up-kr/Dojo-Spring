package com.mashup.dojo.usecase

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.Coin
import com.mashup.dojo.domain.CoinUseDetail
import com.mashup.dojo.domain.CoinUseDetailId
import com.mashup.dojo.domain.CoinUseType
import com.mashup.dojo.domain.Member
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.MemberPlatform
import com.mashup.dojo.service.CoinService
import com.mashup.dojo.service.MemberService
import com.mashup.dojo.service.PickService
import com.mashup.dojo.service.QuestionService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

interface CoinUseCase {
    data class GetCurrentCoinCommand(val memberId: MemberId)

    data class EarnCoinCommand(val memberId: MemberId, val coinAmount: Long)

    data class EarnCoinByEventCommand(val currentMemberId: MemberId, val fullName: String, val platform: String?, val coinAmount: Long)

    data class CoinBySolvedPick(val amount: Int)

    fun getCurrentCoin(command: GetCurrentCoinCommand): Coin

    fun earnCoin(command: EarnCoinCommand): CoinUseDetailId

    fun earnCoinByEvent(command: EarnCoinByEventCommand): CoinUseDetailId

    fun getCoinBySolvedPickList(memberId: MemberId): CoinBySolvedPick
}

@Component
class DefaultCoinUseCase(
    private val coinService: CoinService,
    private val membersService: MemberService,
    private val questionService: QuestionService,
    private val pickService: PickService,
    @Value("\${dojo.coin.solvedPick}")
    private val provideCoinByCompletePick: Int,
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

    @Transactional
    override fun earnCoinByEvent(command: CoinUseCase.EarnCoinByEventCommand): CoinUseDetailId {
        validAdmin(command.currentMemberId)
        val findMember = findMember(command.fullName, command.platform)
        return earnCoin(CoinUseCase.EarnCoinCommand(findMember.id, command.coinAmount))
    }

    override fun getCoinBySolvedPickList(memberId: MemberId): CoinUseCase.CoinBySolvedPick {
        val operatingQSet =
            questionService.getOperatingQuestionSet()
                ?: throw DojoException.of(DojoExceptionType.QUESTION_SET_OPERATING_NOT_EXIST)

        val solvedPickCount =
            pickService.getSolvedPickList(
                pickerMemberId = memberId,
                questionSetId = operatingQSet.id
            ).size

        return CoinUseCase.CoinBySolvedPick(solvedPickCount * provideCoinByCompletePick)
    }

    // todo 추후 Role을 넣어서 Security에서 관리하도록하면 좋을듯합니다.
    private fun validAdmin(currentMemberId: MemberId) {
        val currentMember =
            membersService.findMemberById(currentMemberId)
                ?: throw DojoException.of(DojoExceptionType.MEMBER_NOT_FOUND)

        val dojo = listOf("한정민", "오예원", "박세원", "임준형", "이현재", "황태규", "최민석", "낭은영", "오시연")

        if (currentMember.fullName !in dojo) {
            throw DojoException.of(DojoExceptionType.AUTHENTICATION_FAILURE, "You Are Not Dojo")
        }
    }

    private fun findMember(
        fullName: String,
        inputPlatform: String?,
    ): Member {
        if (inputPlatform.isNullOrEmpty()) {
            return membersService.findMemberByFullName(fullName)
        } else {
            val platform = MemberPlatform.findByValue(inputPlatform)
            return membersService.findByFullNameAndPlatform(fullName, platform)
        }
    }
}
