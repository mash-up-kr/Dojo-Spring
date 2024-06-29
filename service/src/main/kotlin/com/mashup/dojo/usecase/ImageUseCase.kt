package com.mashup.dojo.usecase

import com.mashup.dojo.domain.Image
import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.service.ImageService
import org.springframework.stereotype.Component

interface ImageUseCase {
    fun loadImage(imageId: ImageId): Image
}

@Component
class ImageUseCaseImpl(
    private val imageService: ImageService,
) : ImageUseCase {
    override fun loadImage(imageId: ImageId): Image {
        return imageService.load(imageId)
    }
}
