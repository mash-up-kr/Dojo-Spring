package com.mashup.dojo.usecase

import com.mashup.dojo.domain.Image
import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.service.ImageService
import org.springframework.stereotype.Component

interface ImageUseCase {
    fun loadImage(imageId: ImageId): Image?

    fun uploadImage(
        uuid: String,
        imageUrl: String,
    ): ImageId
}

@Component
class DefaultImageUseCase(
    private val imageService: ImageService,
) : ImageUseCase {
    override fun loadImage(imageId: ImageId): Image? {
        return imageService.load(imageId)
    }

    override fun uploadImage(
        uuid: String,
        imageUrl: String,
    ): ImageId {
        return imageService.save(uuid = uuid, imageUrl = imageUrl)
    }
}
