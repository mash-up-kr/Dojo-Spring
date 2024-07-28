package com.mashup.dojo

import com.mashup.dojo.config.security.JwtTokenService
import com.mashup.dojo.config.security.MemberPrincipalContextHolder
import com.mashup.dojo.domain.MemberId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberAuthTokenTutorialController(
    private val jwtTokenService: JwtTokenService
) {
    @GetMapping("/public/generate-token/{id}")
    fun generate(@PathVariable id: String): String {
        return jwtTokenService.createToken(MemberId(id)).toString()
    }
    
    @GetMapping("/authentication/test")
    fun test(): String {
       val principal = MemberPrincipalContextHolder.current()
        return principal.name
    }
}
