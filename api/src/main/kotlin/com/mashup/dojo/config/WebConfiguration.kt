package com.mashup.dojo.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfiguration : WebMvcConfigurer {
    @Bean
    fun corsFilter(): FilterRegistrationBean<CorsFilter> {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        // config.addAllowedOrigin("https://me-shit.vercel.app")
        config.addAllowedOrigin("https://maship.vercel.app")
        config.addAllowedOrigin("http://localhost:5173")
        config.addAllowedOrigin("http://localhost:4173")
        config.addAllowedHeader("Content-Type")
        config.addAllowedHeader("Authorization")
        config.addAllowedHeader("X-Requested-With")
        config.addAllowedMethod("GET")
        config.addAllowedMethod("POST")
        config.addAllowedMethod("PUT")
        config.addAllowedMethod("PATCH")
        config.addAllowedMethod("DELETE")
        config.addAllowedMethod("OPTIONS")
        config.addExposedHeader("Authorization")
        source.registerCorsConfiguration("/**", config)
        
        // CORS 필터 우선순위 최상단 설정
        return FilterRegistrationBean(CorsFilter(source)).apply { 
            order = Int.MIN_VALUE
        }
    }
}
