package com.mashup.dojo.config.security

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType.AUTHENTICATION_FAILURE
import com.mashup.dojo.DojoExceptionType.MEMBER_NOT_FOUND
import com.mashup.dojo.domain.Member
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.service.MemberService
import java.security.Principal

class MemberAuthTokenAuthenticationProvider(
    private val jwtTokenService: JwtTokenService,
    private val memberService: MemberService,
) {
    fun authenticate(authToken: MemberAuthToken): MemberPrincipal {
        if (jwtTokenService.isExpired(authToken)) throw DojoException.of(AUTHENTICATION_FAILURE, "토큰 기간이 만료되었어요. 다시 로그인해주세요.")
        val memberId = jwtTokenService.getMemberId(authToken) ?: throw DojoException.of(AUTHENTICATION_FAILURE, "유효하지 않은 토큰이에요.")
        val member = memberService.findMemberById(memberId) ?: throw DojoException.of(MEMBER_NOT_FOUND)

        return MemberPrincipal(member)
    }
}

class MemberPrincipal(
    private val member: Member,
) : Principal {
    val id: MemberId = member.id

    override fun getName(): String = "member(id:${member.id}/full-name:${member.fullName})"
}
