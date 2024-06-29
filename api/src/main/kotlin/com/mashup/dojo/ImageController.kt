package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.external.aws.ImageContentType
import com.mashup.dojo.external.aws.ImageUploadUrlProvider
import com.mashup.dojo.usecase.ImageUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger { }

@Tag(name = "Image", description = "이미지 API")
@RestController
class ImageController(
    private val imageUploadUrlProvider: ImageUploadUrlProvider,
    private val imageUseCase: ImageUseCase,
) {
    @GetMapping("image-upload-url")
    @Operation(
        summary = "단일 이미지 업로드 정보 조회 (URL, UUID)",
        description = "이미지 파일을 업로드하기 위한 presigned url 과 uuid 를 반환합니다. PUT API를 통해 호출해주세요",
        responses = [
            ApiResponse(responseCode = "200", description = "업로드된 이미지 id")
        ]
    )
    fun uploadInfo(contentType: ImageContentType): DojoApiResponse<ImageUploadUrlResponse> {
        logger.info { "read image upload info, contentType: ${contentType.value}" }

        val uploadUrlInfo = imageUploadUrlProvider.createUploadUrl(contentType)
        imageUseCase.uploadImage(uploadUrlInfo.uuid, uploadUrlInfo.imageUrl)

        return DojoApiResponse.success(
            ImageUploadUrlResponse(
                uuid = uploadUrlInfo.uuid,
                uploadUrl = uploadUrlInfo.uploadUrl.toString()
            )
        )
    }

    data class ImageUploadUrlResponse(
        val uuid: String,
        val uploadUrl: String,
    )
}
