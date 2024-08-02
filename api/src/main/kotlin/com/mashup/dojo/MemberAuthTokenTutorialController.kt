package com.mashup.dojo

import com.mashup.dojo.config.security.JwtTokenService
import com.mashup.dojo.config.security.MemberPrincipalContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * API 호출한 멤버 정보를 받아오는 방법을 설명하는 튜토리얼 API 입니다.
 */
@RestController
class MemberAuthTokenTutorialController(
    private val jwtTokenService: JwtTokenService,
) {
    @GetMapping("/authentication/test")
    fun test(): String {
        // ContextHolder 를 통해 직접 principal 정보를 받아옵니다.
        val principal = MemberPrincipalContextHolder.current()
        return principal.name
    }
}
