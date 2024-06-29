package com.mashup.dojo.domain

@JvmInline
value class ImageId(val value: String)

data class Image(
    val id: ImageId,
    val url: String,
) {
    companion object {
        val MOCK_USER_IMAGE =
            Image(
                id = ImageId("12345678"),
                url = "https://example.com/image/1"
            )
    }
}
