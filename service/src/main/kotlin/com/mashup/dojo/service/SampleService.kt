package com.mashup.dojo.service

import com.mashup.dojo.domain.Sample
import org.springframework.stereotype.Service

@Service
class SampleService {
    fun getSampleId(): Long {
        val sample = Sample(1, "do~jo")
        return sample.id
    }
}
