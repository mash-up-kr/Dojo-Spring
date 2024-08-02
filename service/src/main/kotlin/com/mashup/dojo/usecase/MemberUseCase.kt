package com.mashup.dojo.usecase

import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.MemberGender
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.MemberPlatform
import com.mashup.dojo.service.CoinService
import com.mashup.dojo.service.MemberService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

interface MemberUseCase {
    data class CreateCommand(
        val fullName: String,
        val profileImageId: ImageId?,
        val platform: MemberPlatform,
        val ordinal: Int,
        val gender: MemberGender,
    )

    data class UpdateCommand(
        val memberId: MemberId,
        val profileImageId: ImageId?,
    )

    fun create(command: CreateCommand): MemberId

    fun update(command: UpdateCommand): MemberId
}

@Component
@Transactional(readOnly = true)
class DefaultMemberUseCase(
    private val memberService: MemberService,
    private val coinService: CoinService,
) : MemberUseCase {
    @Transactional
    override fun create(command: MemberUseCase.CreateCommand): MemberId {
        val memberId: MemberId =
            memberService.create(
                MemberService.CreateMember(
                    fullName = command.fullName,
                    profileImageId = command.profileImageId,
                    platform = command.platform,
                    ordinal = command.ordinal,
                    gender = command.gender
                )
            )

        // 가입 시, coin 정보 생성
        coinService.create(memberId)
        return memberId
    }

    @Transactional
    override fun update(command: MemberUseCase.UpdateCommand): MemberId {
        return memberService.update(
            MemberService.UpdateMember(
                memberId = command.memberId,
                profileImageId = command.profileImageId
            )
        )
    }
}
