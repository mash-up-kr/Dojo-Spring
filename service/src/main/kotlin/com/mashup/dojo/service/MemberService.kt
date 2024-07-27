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
import com.mashup.dojo.domain.MemberToken
import com.mashup.dojo.domain.RelationType
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
        to: MemberId,
        from: MemberId,
    ): MemberRelation {
        // 후보자 생성
        // 여기에 필요한 로직을 추가하세요.

        return MemberRelation(to, from, RelationType.FRIEND)
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

        val targetMemberId1 = memberRelation1.to
        val targetMemberId2 = memberRelation2.to
        val targetMemberId3 = memberRelation3.to
        val targetMemberId4 = memberRelation4.to

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
                m.toMember()
            }
    }

    private fun mockMember(memberId: MemberId) =
        Member(
            MemberToken("12345"), memberId, "임준형", "ㅈ", ImageId("123456"), MemberPlatform.SPRING, 14, MemberGender.MALE, 200, LocalDateTime.now(), LocalDateTime.now()
        )
}

private fun Member.toEntity(): MemberEntity {
    return MemberEntity(
        id = id.value,
        token = token.value,
        fullName = fullName,
        secondInitialName = secondInitialName,
        profileImageId = profileImageId?.value,
        platform = platform.name,
        ordinal = ordinal,
        gender = gender.name,
        point = point
    )
}

private fun MemberEntity.toMember(): Member {
    return Member(
        id = MemberId(this.id),
        token = MemberToken(this.token),
        fullName = fullName,
        secondInitialName = secondInitialName,
        profileImageId = profileImageId?.let { ImageId(it) },
        ordinal = ordinal,
        platform = MemberPlatform.findByValue(platform),
        gender = MemberGender.findByValue(gender),
        point = point,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
