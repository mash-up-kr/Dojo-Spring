package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.config.security.JwtTokenService
import com.mashup.dojo.config.security.MemberPrincipalContextHolder
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.MemberRelationId
import com.mashup.dojo.dto.MemberCreateFriendRelationRequest
import com.mashup.dojo.dto.MemberCreateRequest
import com.mashup.dojo.dto.MemberLoginRequest
import com.mashup.dojo.dto.MemberProfileResponse
import com.mashup.dojo.dto.MemberSearchResponse
import com.mashup.dojo.dto.MemberUpdateRequest
import com.mashup.dojo.dto.MyProfileResponse
import com.mashup.dojo.service.ImageService
import com.mashup.dojo.service.MemberService
import com.mashup.dojo.usecase.MemberUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

private val logger = KotlinLogging.logger { }

@Tag(name = "Member", description = "멤버")
@RestController
class MemberController(
    private val memberUseCase: MemberUseCase,
    private val memberService: MemberService,
    private val imageService: ImageService,
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

        // 가입된 멤버에 대해서 기본 관계 생성
        memberUseCase.createDefaultMemberRelation(memberId)

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

    @GetMapping("/member/{memberId}")
    @Operation(
        summary = "타인 멤버 프로필 조회 API",
        description = "멤버의 프로필을 조회하는 API."
    )
    fun getProfile(
        @PathVariable memberId: String,
    ): DojoApiResponse<MemberProfileResponse> {
        val currentMemberId = MemberPrincipalContextHolder.current().id
        val profileResponse =
            memberUseCase.findMemberById(
                targetMemberId = MemberId(memberId),
                currentMemberId = currentMemberId
            )

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

    @GetMapping("/member/me")
    @Operation(
        summary = "본인 프로필 조회 API",
        description = "본인 프로필 조회 API, header 토큰에 의해 본인을 식별해요"
    )
    fun me(): DojoApiResponse<MyProfileResponse> {
        val memberId = MemberPrincipalContextHolder.current().id

        logger.info { "read my profile, $memberId" }
        val member = memberService.findMemberById(memberId) ?: throw DojoException.of(DojoExceptionType.MEMBER_NOT_FOUND)
        val profileImage = imageService.load(member.profileImageId) ?: throw DojoException.of(DojoExceptionType.IMAGE_NOT_FOUND)

        return DojoApiResponse.success(
            MyProfileResponse(
                memberId = member.id.value,
                profileImageUrl = profileImage.url,
                memberName = member.fullName,
                platform = member.platform.name,
                ordinal = member.ordinal,
                // TODO MOCK 값들 채우기
                pickCount = 10,
                friendCount = 20,
                coinCount = 0
            )
        )
    }

    // ToDo 로직 연결 후 추후 제거
    @GetMapping("/member/mock/{memberId}")
    @Operation(
        summary = "타인 멤버 프로필 조회 API",
        description = "멤버의 프로필을 조회하는 API."
    )
    fun getProfileMock(
        @PathVariable memberId: String,
    ): DojoApiResponse<MemberProfileResponse> {
        val profileResponse = memberUseCase.findMemberByIdMock(MemberId(memberId))

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

    @PostMapping("/member/friend")
    @Operation(
        summary = "친구(팔로우)추가 API",
        description = "친구(팔로우) 관계 생성 API, 친구 추가 기능에 대해서 from 이 to 를 follow 합니다. 이미 follow가 존재한다면 예외를 반환해요",
        responses = [
            ApiResponse(responseCode = "200", description = "생성된 관계 id")
        ]
    )
    fun createFriend(
        @RequestBody request: MemberCreateFriendRelationRequest,
    ): DojoApiResponse<MemberRelationId> {
        return DojoApiResponse.success(memberUseCase.updateFriendRelation(MemberUseCase.UpdateFriendCommand(request.fromMemberId, request.toMemberId)))
    }

    @GetMapping("/member/my-space/pick")
    @Operation(
        summary = "마이 스페이스 내가 받은 픽 API",
        description = "마이스페이스 탭 중 내가 받은 픽의 대한 API입니다. 공동 등수를 자동으로 계산하고 반환합니다. Pick이 많은 순서대로 등수를 나누고, 최신순, 내림차순으로 정렬합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "마이스페이스 - 내가 받은 픽 Response",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Example Response",
                                value = EXAMPLE_VALUE
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun mySpace(): DojoApiResponse<MySpacePickResponse> {
        val memberId = MemberPrincipalContextHolder.current().id
        val receivedMySpacePicks = memberUseCase.receivedMySpacePicks(memberId)
        val response =
            receivedMySpacePicks.map {
                SpacePickDetail(
                    pickId = it.pickId.value,
                    rank = it.rank,
                    pickContent = it.pickContent,
                    pickCount = it.pickCount,
                    createdAt = it.createdAt
                )
            }

        return DojoApiResponse.success(
            MySpacePickResponse(response)
        )
    }

    @GetMapping("/member/friend-space/{friendId}/pick")
    @Operation(
        summary = "친구 스페이스 친구가 받은 픽 API",
        description = "친구 스페이스 탭 중 친구가 받은 픽의 대한 API입니다. 공동 등수를 자동으로 계산하고 반환합니다. Pick이 많은 순서대로 등수를 나누고, 최신순, 내림차순으로 정렬합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "친구스페이스 - 친구가 받은 픽 Response",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Example Response",
                                value = EXAMPLE_VALUE
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun friendSpace(
        @PathVariable friendId: String,
    ): DojoApiResponse<FriendSpacePickResponse> {
        val receivedMySpacePicks = memberUseCase.receivedFriendSpacePicks(MemberId(friendId))
        val response =
            receivedMySpacePicks.map {
                SpacePickDetail(
                    pickId = it.pickId.value,
                    rank = it.rank,
                    pickContent = it.pickContent,
                    pickCount = it.pickCount,
                    createdAt = it.createdAt
                )
            }

        return DojoApiResponse.success(
            FriendSpacePickResponse(response)
        )
    }

    @GetMapping("/member/search")
    @Operation(
        summary = "멤버 검색 API",
        description = "친구, 친구 아닌 사람 모두 검색 가능한 통합 검색 기능입니다."
    )
    fun searchMember(
        @RequestParam keyword: String,
    ): DojoApiResponse<List<MemberSearchResponse>> {
        val memberId = MemberPrincipalContextHolder.current().id
        val result = memberUseCase.searchMember(memberId, keyword)
        return DojoApiResponse.success(
            result.map {
                MemberSearchResponse(
                    memberId = it.memberId,
                    profileImageUrl = it.profileImageUrl,
                    memberName = it.memberName,
                    platform = it.platform,
                    ordinal = it.ordinal,
                    isFriend = it.isFriend
                )
            }
        )
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

    data class SpacePickDetail(
        @Schema(description = "픽의 고유 ID", example = "pickId1")
        val pickId: String,
        @Schema(description = "픽의 순위", example = "1")
        val rank: Int,
        @Schema(description = "픽의 내용", example = "대충 작업해도 퀄리티 잘 내오는 사람은?")
        val pickContent: String,
        @Schema(description = "픽의 투표 수", example = "999")
        val pickCount: Int,
        @Schema(description = "픽이 생성된 날짜", example = "2024-08-12T17:18:52.132Z")
        val createdAt: LocalDateTime,
    )

    data class MySpacePickResponse(
        @ArraySchema(arraySchema = Schema(description = "MySpace 내가 받은 픽 목록"))
        val mySpaceResponses: List<SpacePickDetail>,
    )

    data class FriendSpacePickResponse(
        @ArraySchema(arraySchema = Schema(description = "FriendSpace 친구가 받은 픽 목록"))
        val friendSpaceResponses: List<SpacePickDetail>,
    )

    companion object {
        private const val EXAMPLE_VALUE = """
                        {
                          "success": true,
                          "data": {
                            "mySpaceResponses": [
                              {
                                "pickId": "pickId1",
                                "rank": 1,
                                "pickContent": "대충 작업해도 퀄리티 잘 내오는 사람은?",
                                "pickCount": 999,
                                "createdAt": "2024-08-12T17:18:52.132Z"
                              },
                              {
                                "pickId": "pickId2",
                                "rank": 2,
                                "pickContent": "매쉬업에서 운동 제일 잘할 것 같은 사람은?",
                                "pickCount": 500,
                                "createdAt": "2024-08-12T17:18:52.132Z"
                              },
                              {
                                "pickId": "pickId3",
                                "rank": 3,
                                "pickContent": "매쉬업에서 이성으로 소개시켜주고 싶은 사람은?",
                                "pickCount": 300,
                                "createdAt": "2024-08-12T17:18:52.132Z"
                              }
                            ]
                          },
                          "error": {
                            "code": "string",
                            "message": "string"
                          }
                        }
                        """
    }
}
