package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.usecase.SampleUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/test")
@RestController
class SampleController(
    private val sampleUseCase: SampleUseCase,
) {
    @GetMapping
    fun test(): DojoApiResponse<Long> {
        return DojoApiResponse.success(sampleUseCase.getSampleId())
    }
}
