package com.mashup.dojo.config

import com.mashup.dojo.config.security.JwtTokenService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebSecurityConfiguration {
    @Bean
    fun jwtTokenService() = JwtTokenService("dojo-secret-dojo-secret-dojo-secret")
}
