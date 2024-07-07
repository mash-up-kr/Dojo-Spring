package com.mashup.dojo

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {
    @Hidden
    @GetMapping("/health")
    fun health(): String {
        return "OK"
    }
}
