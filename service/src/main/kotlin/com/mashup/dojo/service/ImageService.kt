package com.mashup.dojo.service

import com.mashup.dojo.ImageEntity
import com.mashup.dojo.ImageRepository
import com.mashup.dojo.domain.Image
import com.mashup.dojo.domain.ImageId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

interface ImageService {
    fun load(imageId: ImageId): Image?

    fun save(
        uuid: String,
        imageUrl: String,
    ): ImageId

    fun loadAllByIds(imageIds: List<ImageId>): List<Image>
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

    override fun loadAllByIds(imageIds: List<ImageId>): List<Image> {
        return imageRepository.findAllById(imageIds.map { it.value }).map { it.toImage() }
    }

    override fun load(imageId: ImageId): Image? {
        return imageRepository.findById(imageId.value).getOrNull()?.toImage()
    }

    companion object {
        val SAMPLE_IMAGE = Image(ImageId("image"), "urlurl")
    }
}

private fun ImageEntity.toImage(): Image {
    return Image(
        id = ImageId(id),
        url = url
    )
}
