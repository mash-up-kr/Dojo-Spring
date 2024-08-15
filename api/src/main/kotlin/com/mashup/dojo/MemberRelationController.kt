package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.config.security.MemberPrincipalContextHolder
import com.mashup.dojo.dto.FriendInfoResponse
import com.mashup.dojo.usecase.MemberRelationUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "MemberRelation", description = "멤버 관계")
@RestController
class MemberRelationController(
    private val memberRelationUseCase: MemberRelationUseCase,
) {
    @GetMapping("/friends")
    @Operation(
        summary = "내 친구 목록 조회 API",
        description = "내 친구 목록을 조회하는 API"
    )
    fun getFriends(): DojoApiResponse<List<FriendInfoResponse>> {
        val memberId = MemberPrincipalContextHolder.current().id

        val friendInfos = memberRelationUseCase.getFriends(memberId)

        return DojoApiResponse.success(
            friendInfos.map {
                FriendInfoResponse(
                    memberId = it.memberId,
                    profileImageUrl = it.profileImageUrl,
                    memberName = it.memberName,
                    platform = it.platform,
                    ordinal = it.ordinal
                )
            }
        )
    }

    @GetMapping("/recommend-friends")
    @Operation(
        summary = "추천 친구 목록 조회 API",
        description = "추천 친구 목록을 조회하는 API"
    )
    fun getRecommendFriends(): DojoApiResponse<List<FriendInfoResponse>> {
        val memberId = MemberPrincipalContextHolder.current().id

        val recommendFriendInfos = memberRelationUseCase.getRecommendFriends(memberId)

        return DojoApiResponse.success(
            recommendFriendInfos.map {
                FriendInfoResponse(
                    memberId = it.memberId,
                    profileImageUrl = it.profileImageUrl,
                    memberName = it.memberName,
                    platform = it.platform,
                    ordinal = it.ordinal
                )
            }
        )
    }
}
