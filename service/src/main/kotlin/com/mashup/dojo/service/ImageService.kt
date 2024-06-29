package com.mashup.dojo.service

import com.mashup.dojo.domain.Image
import com.mashup.dojo.domain.ImageId
import org.springframework.stereotype.Service

interface ImageService {
    fun load(imageId: ImageId): Image
}

@Service
class MockImageService() : ImageService {
    override fun load(imageId: ImageId): Image {
        return Image.MOCK_IMAGE
    }
}
