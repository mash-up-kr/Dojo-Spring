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
    fun findFriendsIds(currentMemberId: MemberId): List<Candidate>

    fun findMemberById(memberId: MemberId): Member
}

@Service
class MemberServiceImpl : MemberService {
    private fun initMemberRelation(to: Long): MemberRelation {
        // 후보자 생성
        // 여기에 필요한 로직을 추가하세요.

        val currentMemberId = MemberId(90L)
        val toMemberId = MemberId(to)
        return MemberRelation(currentMemberId, toMemberId, RelationType.FRIEND)
    }

    override fun findFriendsIds(currentMemberId: MemberId): List<Candidate> {
        val memberRelation1 = initMemberRelation(20L)
        val memberRelation2 = initMemberRelation(30L)
        val memberRelation3 = initMemberRelation(40L)
        val memberRelation4 = initMemberRelation(50L)

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
        return initMember(memberId)
    }

    private fun initMember(memberId: MemberId) =
        Member(
            memberId, "임준형", "ㅈ", "profile_image_url",
            MemberPlatform.SPRING, 14, MemberGender.MALE, 200, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()
        )
}
