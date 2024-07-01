package com.mashup.dojo.usecase

import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.MemberGender
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.MemberPlatform
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

    fun create(command: CreateCommand): MemberId
}

@Component
@Transactional(readOnly = true)
class DefaultMemberUseCase(
    private val memberService: MemberService,
) : MemberUseCase {
    @Transactional
    override fun create(command: MemberUseCase.CreateCommand): MemberId {
        return memberService.create(
            MemberService.CreateMember(
                fullName = command.fullName,
                profileImageId = command.profileImageId,
                platform = command.platform,
                ordinal = command.ordinal,
                gender = command.gender
            )
        )
    }
}
