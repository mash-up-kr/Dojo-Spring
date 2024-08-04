package com.mashup.dojo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfiguration : WebMvcConfigurer {
    // override fun addCorsMappings(registry: CorsRegistry) {
    //     registry.addMapping("/**")
    //         .allowedOrigins("http://localhost:5173")
    //         .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
    // }

    @Bean
    fun corsFilter(): CorsFilter {
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
        return CorsFilter(source)
    }
}
