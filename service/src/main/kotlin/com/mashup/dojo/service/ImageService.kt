package com.mashup.dojo.service

import com.mashup.dojo.ImageEntity
import com.mashup.dojo.ImageRepository
import com.mashup.dojo.domain.Image
import com.mashup.dojo.domain.ImageId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface ImageService {
    fun load(imageId: ImageId): Image?

    fun save(
        uuid: String,
        imageUrl: String,
    ): ImageId
}

@Transactional(readOnly = true)
@Service
class DefaultImageService(
    private val imageRepository: ImageRepository,
) : ImageService {
    @Transactional
    override fun save(
        uuid: String,
        imageUrl: String,
    ): ImageId {
        val entity = ImageEntity(id = uuid, url = imageUrl)
        val saved = imageRepository.save(entity)
        return ImageId(saved.id)
    }

    override fun load(imageId: ImageId): Image? {
        TODO("Not yet implemented")
    }
}
