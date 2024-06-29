package com.mashup.dojo.domain

@JvmInline
value class ImageId(val value: Long)

data class Image(
    val id: ImageId,
    val url: String,
) {
    companion object {
        val MOCK_USER_IMAGE =
            Image(
                id = ImageId(1),
                url = "https://example.com/image/1"
            )
    }
}
