package com.mashup.dojo.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class GlobalCacheConfig(
    @Value("\${spring.data.redis.host}")
    private val redisHost: String,
    @Value("\${spring.data.redis.port}")
    private val redisPort: Int,
    @Value("\${spring.data.redis.password}")
    private val redisPassword: String,
) {
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val redisConfiguration = RedisStandaloneConfiguration().apply {
            hostName = redisHost
            port = redisPort
            password = RedisPassword.of(redisPassword)
        }

        return LettuceConnectionFactory(
            redisConfiguration,
            LettuceClientConfiguration.builder().useSsl().build()
        )
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory?): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            connectionFactory = redisConnectionFactory
        }
    }
}
