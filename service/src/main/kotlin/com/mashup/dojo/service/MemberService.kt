package com.mashup.dojo.service

import com.mashup.dojo.domain.Candidate
import com.mashup.dojo.domain.Member
import com.mashup.dojo.domain.MemberGender
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.MemberPlatform
import com.mashup.dojo.domain.MemberRelation
import com.mashup.dojo.domain.RelationType
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface MemberService {
    fun getCandidates(currentMemberId: MemberId): List<Candidate>

    fun findMemberById(memberId: MemberId): Member
}

@Service
class DefaultMemberService : MemberService {
    private fun mockMemberRelation(
        to: MemberId,
        from: MemberId,
    ): MemberRelation {
        // 후보자 생성
        // 여기에 필요한 로직을 추가하세요.

        return MemberRelation(to, from, RelationType.FRIEND)
    }

    override fun getCandidates(currentMemberId: MemberId): List<Candidate> {
        val memberRelation1 = mockMemberRelation(currentMemberId, MemberId(20))
        val memberRelation2 = mockMemberRelation(currentMemberId, MemberId(30))
        val memberRelation3 = mockMemberRelation(currentMemberId, MemberId(40))
        val memberRelation4 = mockMemberRelation(currentMemberId, MemberId(50))
        val memberRelation5 = mockMemberRelation(currentMemberId, MemberId(60))
        val memberRelation6 = mockMemberRelation(currentMemberId, MemberId(70))

        /**
         * ToDo
         * 친구들 중 랜덤 4명 뽑기
         */

        // Mock, 랜덤으로 뽑은 4명.
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

    override fun findMemberById(memberId: MemberId): Member {
        // memberRepository find(MemberId)
        return mockMember(memberId)
    }

    private fun mockMember(memberId: MemberId) =
        Member(
            memberId, "임준형", "ㅈ", "profile_image_url",
            MemberPlatform.SPRING, 14, MemberGender.MALE, 200, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()
        )
}
