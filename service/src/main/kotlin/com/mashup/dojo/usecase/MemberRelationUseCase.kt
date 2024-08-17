package com.mashup.dojo.usecase

import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.service.ImageService
import com.mashup.dojo.service.MemberRelationService
import com.mashup.dojo.service.MemberService
import com.mashup.dojo.usecase.MemberRelationUseCase.FriendInfo
import org.springframework.stereotype.Component

interface MemberRelationUseCase {
    data class FriendInfo(
        val memberId: String,
        val profileImageUrl: String,
        val memberName: String,
        val platform: String,
        val ordinal: Int,
    )

    fun getFriends(memberId: MemberId): List<FriendInfo>

    fun getRecommendFriends(memberId: MemberId): List<FriendInfo>
}

@Component
class DefaultMemberRelationUseCase(
    private val memberRelationService: MemberRelationService,
    private val memberService: MemberService,
    private val imageService: ImageService,
) : MemberRelationUseCase {
    override fun getFriends(memberId: MemberId): List<FriendInfo> {
        val friendIds = memberRelationService.getFriendRelationIds(memberId)
        val friends =
            memberService.findAllByIds(friendIds).sortedBy {
                // 가나다순 정렬
                it.fullName
            }

        val images = imageService.loadAllByIds(friends.map { it.profileImageId })
        val imageMap = images.associateBy { it.id.value }

        return friends.map { friend ->
            FriendInfo(
                memberId = friend.id.value,
                profileImageUrl = imageMap[friend.profileImageId.value]?.url.let { "" },
                memberName = friend.fullName,
                platform = friend.platform.name,
                ordinal = friend.ordinal
            )
        }
    }

    override fun getRecommendFriends(memberId: MemberId): List<FriendInfo> {
        val recommendFriendIds = memberRelationService.getAccompanyRelationIds(memberId)
        val recommendFriends = memberService.findAllByIds(recommendFriendIds)

        val images = imageService.loadAllByIds(recommendFriends.map { it.profileImageId })
        val imageMap = images.associateBy { it.id.value }

        return recommendFriends.map { friend ->
            FriendInfo(
                memberId = friend.id.value,
                profileImageUrl = imageMap[friend.profileImageId.value]?.url.let { "" },
                memberName = friend.fullName,
                platform = friend.platform.name,
                ordinal = friend.ordinal
            )
        }
    }
}
