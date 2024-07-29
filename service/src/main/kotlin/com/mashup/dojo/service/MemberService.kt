package com.mashup.dojo.service

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.MemberEntity
import com.mashup.dojo.MemberRepository
import com.mashup.dojo.domain.Candidate
import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.Member
import com.mashup.dojo.domain.MemberGender
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.MemberPlatform
import com.mashup.dojo.domain.MemberRelation
import com.mashup.dojo.domain.MemberRelationId
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface MemberService {
    fun getCandidates(currentMemberId: MemberId): List<Candidate>

    fun findMemberById(memberId: MemberId): Member?

    fun create(command: CreateMember): MemberId

    fun update(command: UpdateMember): MemberId

    fun findAllMember(): List<Member>

    data class CreateMember(
        val fullName: String,
        val profileImageId: ImageId?,
        val platform: MemberPlatform,
        val ordinal: Int,
        val gender: MemberGender,
    )

    data class UpdateMember(
        val memberId: MemberId,
        val profileImageId: ImageId?,
    )
}

@Service
class DefaultMemberService(
    private val memberRepository: MemberRepository,
) : MemberService {
    private fun mockMemberRelation(
        fromId: MemberId,
        toId: MemberId,
    ): MemberRelation {
        // 후보자 생성
        // 여기에 필요한 로직을 추가하세요.

        return MemberRelation(
            id = MemberRelationId("MemberRelationId"),
            fromId = fromId,
            toId = toId,
            lastUpdatedAt = LocalDateTime.now()
        )
    }

    override fun getCandidates(requestId: MemberId): List<Candidate> {
        val memberRelation1 = mockMemberRelation(requestId, MemberId("20"))
        val memberRelation2 = mockMemberRelation(requestId, MemberId("30"))
        val memberRelation3 = mockMemberRelation(requestId, MemberId("40"))
        val memberRelation4 = mockMemberRelation(requestId, MemberId("50"))
        val memberRelation5 = mockMemberRelation(requestId, MemberId("60"))
        val memberRelation6 = mockMemberRelation(requestId, MemberId("70"))

        /**
         * ToDo
         * 친구들 중 랜덤 4명 뽑기
         * Mock, 랜덤으로 뽑은 4명.
         *
         */

        val targetMemberId1 = memberRelation1.toId
        val targetMemberId2 = memberRelation2.toId
        val targetMemberId3 = memberRelation3.toId
        val targetMemberId4 = memberRelation4.toId

        val candidate1 = Candidate(targetMemberId1, "한씨", 1)
        val candidate2 = Candidate(targetMemberId2, "오씨", 2)
        val candidate3 = Candidate(targetMemberId3, "박씨", 3)
        val candidate4 = Candidate(targetMemberId4, "김", 4)

        return listOf(candidate1, candidate2, candidate3, candidate4)
    }

    override fun findMemberById(memberId: MemberId): Member? {
        // memberRepository find(MemberId)
        return mockMember(memberId)
    }

    override fun create(command: MemberService.CreateMember): MemberId {
        val member =
            Member.create(
                fullName = command.fullName,
                profileImageId = command.profileImageId,
                platform = command.platform,
                gender = command.gender,
                ordinal = command.ordinal
            )

        val id = memberRepository.save(member.toEntity()).id
        return MemberId(id)
    }

    override fun update(command: MemberService.UpdateMember): MemberId {
        val member = findMemberById(command.memberId) ?: throw DojoException.of(DojoExceptionType.MEMBER_NOT_FOUND)

        val id =
            memberRepository.save(
                member.update(
                    profileImageId = command.profileImageId
                ).toEntity()
            ).id

        return MemberId(id)
    }

    override fun findAllMember(): List<Member> {
        return memberRepository.findAll()
            .map { m ->
                val platform = MemberPlatform.findByValue(m.platform)
                val gender = MemberGender.findByValue(m.gender)
                val imageId = m.profileImageId?.let { ImageId(it) }

                Member.convertToMember(
                    id = m.id,
                    fullName = m.fullName,
                    secondInitialName = m.secondInitialName,
                    profileImageId = imageId,
                    ordinal = m.ordinal,
                    platform = platform,
                    gender = gender,
                    createdAt = m.createdAt,
                    updatedAt = m.updatedAt
                )
            }
    }

    private fun mockMember(memberId: MemberId) =
        Member(
            memberId, "임준형", "ㅈ", ImageId("123456"), MemberPlatform.SPRING, 14, MemberGender.MALE, LocalDateTime.now(), LocalDateTime.now()
        )
}

private fun Member.toEntity(): MemberEntity {
    return MemberEntity(
        id = id.value,
        fullName = fullName,
        secondInitialName = secondInitialName,
        profileImageId = profileImageId?.value,
        platform = platform.name,
        ordinal = ordinal,
        gender = gender.name
    )
}
