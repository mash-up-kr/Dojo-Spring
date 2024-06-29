package com.mashup.dojo.external.aws

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3Client
import java.net.URL
import java.time.Duration
import java.util.Date
import java.util.UUID

interface ImageUploadUrlProvider {
    fun createUploadUrl(contentType: ImageContentType): ImageUploadUrl
}

data class ImageUploadUrl(
    val uuid: String,
    val imageUrl: String,
    val uploadUrl: URL,
)

enum class ImageContentType(val value: String) {
    PNG("png"),
    JPEG("jpeg"),
}

class S3ImageUploadUrlProvider(
    private val client: AmazonS3Client,
) : ImageUploadUrlProvider {
    override fun createUploadUrl(contentType: ImageContentType): ImageUploadUrl {
        val uuid = UUID.randomUUID().toString()
        val path = "images/$uuid.${contentType.value}"

        val uploadUrl =
            client.generatePresignedUrl(
                IMAGE_BUCKET_NAME,
                path,
                Date(System.currentTimeMillis() + Duration.ofMinutes(5).toMillis()),
                HttpMethod.PUT
            )

        return ImageUploadUrl(
            uuid = uuid,
            imageUrl = "https://$IMAGE_BUCKET_NAME.s3.ap-northeast-2.amazonaws.com/$path",
            uploadUrl = uploadUrl
        )
    }

    companion object {
        private const val IMAGE_BUCKET_NAME = "dojo-backend-source-bundle"
    }
}
