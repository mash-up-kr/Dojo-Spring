package com.mashup.dojo.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebSecurityConfiguration {
    @Bean
    fun jwtTokenService() = JwtTokenService("dojo-secret-dojo-secret-dojo-secret")

    /**
     * 임시 제거
     */
//    @Bean
//    fun tokenBasedAuthenticationFilter(
//        memberService: MemberService,
//        jwtTokenService: JwtTokenService,
//    ): FilterRegistrationBean<MemberAuthTokenAuthenticationFilter> {
//        val authenticationProvider = MemberAuthTokenAuthenticationProvider(jwtTokenService, memberService)
//
//        val filter = MemberAuthTokenAuthenticationFilter(authenticationProvider)
//
//        return FilterRegistrationBean(filter).apply {}
//    }
}
