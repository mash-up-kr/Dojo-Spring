package com.mashup.dojo.usecase

import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.UserImage
import com.mashup.dojo.service.ImageService
import org.springframework.stereotype.Component

interface ImageUseCase {
    fun loadImage(imageId: ImageId): UserImage
}

@Component
class DefaultImageUseCase(
    private val imageService: ImageService,
) : ImageUseCase {
    override fun loadImage(imageId: ImageId): UserImage {
        return imageService.load(imageId)
    }
}
