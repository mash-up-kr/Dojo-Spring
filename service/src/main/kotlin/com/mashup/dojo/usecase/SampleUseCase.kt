package com.mashup.dojo.usecase

import com.mashup.dojo.service.SampleService
import org.springframework.stereotype.Component

@Component
class SampleUseCase(
    private val sampleService: SampleService,
) {
    fun getSampleId(): Long {
        return sampleService.getSample().id
    }
}
