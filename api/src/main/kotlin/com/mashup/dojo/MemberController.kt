package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.config.security.JwtTokenService
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.dto.MemberCreateRequest
import com.mashup.dojo.dto.MemberLoginRequest
import com.mashup.dojo.dto.MemberProfileResponse
import com.mashup.dojo.dto.MemberUpdateRequest
import com.mashup.dojo.service.MemberService
import com.mashup.dojo.usecase.MemberUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger { }

@Tag(name = "Member", description = "멤버")
@RestController
class MemberController(
    private val memberUseCase: MemberUseCase,
    private val memberService: MemberService,
    private val jwtTokenService: JwtTokenService,
) {
    @PostMapping("/public/member")
    @Operation(
        summary = "[PUBLIC] 멤버 가입 API",
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

    @PostMapping("/public/member-login")
    @Operation(
        summary = "[PUBLIC] 멤버 로그인 API",
        description = "멤버 로그인 API, ID 값으로만 로그인하며 token을 발급받는 용도",
        responses = [
            ApiResponse(responseCode = "200", description = "생성된 멤버의 ID")
        ]
    )
    fun login(
        @RequestBody request: MemberLoginRequest,
    ): DojoApiResponse<MemberLoginResponse> {
        val id = MemberId(request.id)
        logger.info { "member-login, id: $id" }

        val member = memberService.findMemberById(id) ?: throw DojoException.of(DojoExceptionType.MEMBER_NOT_FOUND)
        val authToken = jwtTokenService.createToken(id)
        return DojoApiResponse.success(MemberLoginResponse(id, authToken.credentials))
    }

    @GetMapping("/member")
    @Operation(
        summary = "타인 멤버 프로필 조회 API",
        description = "멤버의 프로필을 조회하는 API.",
    )
    fun getProfile(
        @RequestParam memberId: String,
    ): DojoApiResponse<MemberProfileResponse> {
        val profileResponse = memberUseCase.findMemberById(MemberId(memberId))

        return DojoApiResponse.success(
            MemberProfileResponse(
                memberId = profileResponse.memberId.value,
                profileImageUrl = profileResponse.profileImageUrl,
                memberName = profileResponse.memberName,
                platform = profileResponse.platform,
                ordinal = profileResponse.ordinal,
                isFriend = profileResponse.isFriend,
                pickCount = profileResponse.pickCount,
                friendCount = profileResponse.friendCount
            )
        )
    }

    @PatchMapping("/member/{id}")
    @Operation(
        summary = "멤버 정보 갱신 API",
        description = "멤버 정보 수정 시 사용하는 API. 수정될 요소만 not-null로 받아요. null로 들어온 프로퍼티는 기존 값을 유지해요.",
        responses = [
            ApiResponse(responseCode = "200", description = "갱신된 멤버의 ID")
        ]
    )
    fun update(
        @PathVariable id: String,
        @RequestBody request: MemberUpdateRequest,
    ): DojoApiResponse<MemberUpdateResponse> {
        logger.info { "update member, member-id: $id, request: $request" }

        val memberId =
            memberUseCase.update(
                MemberUseCase.UpdateCommand(
                    memberId = MemberId(id),
                    profileImageId = request.profileImageId
                )
            )

        return DojoApiResponse.success(MemberUpdateResponse(memberId))
    }

    data class MemberCreateResponse(
        val id: MemberId,
    )

    data class MemberUpdateResponse(
        val id: MemberId,
    )

    data class MemberLoginResponse(
        val id: MemberId,
        val authToken: String,
    )
}
