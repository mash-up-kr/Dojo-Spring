package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.dto.MemberCreateRequest
import com.mashup.dojo.usecase.MemberUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger { }

@Tag(name = "Member", description = "멤버")
@RestController
class MemberController(
    private val memberUseCase: MemberUseCase,
) {
    @PostMapping("/member")
    @Operation(
        summary = "멤버 가입 API",
        description = "멤버 가입 시 사용하는 API. 현재 ID 제외(auto generation) 별도의 unique 값은 없어요.",
        responses = [
            ApiResponse(responseCode = "200", description = "생성된 멤버의 ID")
        ]
    )
    fun create(
        @RequestBody request: MemberCreateRequest,
    ): DojoApiResponse<MemberCreateResponse> {
        logger.info { "create member, request: $request" }

        val memberId =
            memberUseCase.create(
                MemberUseCase.CreateCommand(
                    fullName = request.fullName,
                    profileImageId = request.profileImageId,
                    platform = request.platform,
                    ordinal = request.ordinal,
                    gender = request.gender
                )
            )

        return DojoApiResponse.success(MemberCreateResponse(memberId))
    }

    data class MemberCreateResponse(
        val id: MemberId,
    )
}
