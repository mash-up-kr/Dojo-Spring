package com.mashup.dojo.api

import com.mashup.dojo.usecase.TestUsecase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestRestController(
    private val testUsecase: TestUsecase
) {

    @GetMapping("/test")
    fun test(): String {
        return testUsecase.dojoName()
    }
}
