package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.usecase.SampleUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/test")
@RestController
class SampleController(
    private val sampleUseCase: SampleUseCase,
) {
    /**
     * 스웨거 예시
     * http://localhost:8080/swagger-ui/index.html
     */
    @GetMapping("/{id}")
    @Tag(name = "Sample", description = "Sample입니다.")
    @Operation(
        summary = "Sample API",
        description = "Sample API 입니다요",
        responses = [
            ApiResponse(responseCode = "200", description = "샘플 API 성공하면 어쩌구 데이터가 내려갑니다.")
        ]
    )
    fun test(
        @PathVariable id: Long,
    ): DojoApiResponse<Long> {
        return DojoApiResponse.success(sampleUseCase.getSampleId())
    }
}
