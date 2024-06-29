package com.mashup.dojo.service

import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.UserImage
import org.springframework.stereotype.Service

interface ImageService {
    fun load(imageId: ImageId): UserImage
}

@Service
class MockImageService() : ImageService {
    override fun load(imageId: ImageId): UserImage {
        return UserImage.MOCK_USER_IMAGE
    }
}
