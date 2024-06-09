package com.mashup.dojo.service

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.SampleEntity
import com.mashup.dojo.SampleRepository
import com.mashup.dojo.domain.Sample
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class SampleService(
    private val sampleRepository: SampleRepository,
) {
    fun getSample(): Sample {
        val sampleEntity = sampleRepository.findByIdOrNull(0L) ?: throw DojoException.of(DojoExceptionType.NOT_EXIST)
        return sampleEntity.buildDomain()
    }

    private fun SampleEntity.buildDomain(): Sample {
        return Sample(
            id = id,
            name = name,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isDeleted = isDeleted
        )
    }
}
