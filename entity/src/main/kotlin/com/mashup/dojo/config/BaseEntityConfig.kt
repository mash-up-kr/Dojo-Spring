package com.mashup.dojo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.Optional
import java.util.UUID

@EnableJpaAuditing
@Configuration
class BaseEntityConfig {
    @Bean
    fun auditorProvider(): AuditorAware<String> {
        return AuditorAware {
            /*
            val authentication: Authentication? = SecurityContextHolder.getContext().authentication
            if (authentication == null) {
                return@AuditorAware Optional.of("AnonymousNULL")
            }

            val principal: Any = authentication.principal
            if (principal is Member) {
                val email: String? = principal.email
                return@AuditorAware Optional.ofNullable(email)
            }
            // principal이 Member 타입이 아닌 경우의 처리
            Optional.of("AnonymousNOT_TYPE")
             */

            // 임시로 UUID 생성하여 반환
            Optional.of(UUID.randomUUID().toString())
        }
    }
}
