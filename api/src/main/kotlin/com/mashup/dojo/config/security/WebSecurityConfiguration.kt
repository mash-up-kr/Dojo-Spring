package com.mashup.dojo.config.security

import com.mashup.dojo.service.MemberService
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebSecurityConfiguration {

    @Bean
    fun jwtTokenService() = JwtTokenService("secret-secret-secret-dojo-secret")
    
    @Bean
    fun tokenBasedAuthenticationFilter(
        memberService: MemberService,
        jwtTokenService: JwtTokenService,
    ): FilterRegistrationBean<MemberAuthTokenAuthenticationFilter> {

        val authenticationProvider = MemberAuthTokenAuthenticationProvider(jwtTokenService, memberService)

        val filter = MemberAuthTokenAuthenticationFilter(authenticationProvider)

        return FilterRegistrationBean(filter).apply {}
    }
}
