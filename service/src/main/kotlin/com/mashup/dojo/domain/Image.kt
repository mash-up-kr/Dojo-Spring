package com.mashup.dojo.domain

@JvmInline
value class ImageId(val value: Long)

sealed class Image

data class UserImage(
    val id: ImageId,
    val url: String,
) : Image() {
    companion object {
        val MOCK_USER_IMAGE =
            UserImage(
                id = ImageId(1),
                url = "https://example.com/image/1"
            )
    }
}

data class EmojiImage(
    val id: ImageId,
    val url: String,
) : Image() {
    companion object {
        val MOCK_EMOJI_IMAGE =
            EmojiImage(
                id = ImageId(2),
                url = "https://example.com/image/2"
            )
    }
}
