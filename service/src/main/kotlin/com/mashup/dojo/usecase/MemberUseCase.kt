package com.mashup.dojo.usecase

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.MemberGender
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.MemberPlatform
import com.mashup.dojo.domain.MemberRelationId
import com.mashup.dojo.domain.RelationType
import com.mashup.dojo.service.CoinService
import com.mashup.dojo.service.ImageService
import com.mashup.dojo.service.MemberRelationService
import com.mashup.dojo.service.MemberService
import com.mashup.dojo.service.PickService
import com.mashup.dojo.service.ProfileImageProperties
import com.mashup.dojo.service.calculateRanks
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

interface MemberUseCase {
    data class CreateCommand(
        val fullName: String,
        val profileImageId: ImageId?,
        val platform: MemberPlatform,
        val ordinal: Int,
        val gender: MemberGender,
    )

    data class UpdateCommand(
        val memberId: MemberId,
        val profileImageId: ImageId?,
    )

    data class ProfileResponse(
        val memberId: MemberId,
        val profileImageUrl: String,
        val memberName: String,
        val platform: String,
        val ordinal: Int,
        val isFriend: Boolean,
        val pickCount: Int,
        val friendCount: Int,
    )

    data class UpdateFriendCommand(
        val fromId: MemberId,
        val toId: MemberId,
    )

    data class MemberSearchInfo(
        val memberId: String,
        val profileImageUrl: String,
        val memberName: String,
        val platform: String,
        val ordinal: Int,
        val isFriend: Boolean,
    )

    fun create(command: CreateCommand): MemberId

    fun update(command: UpdateCommand): MemberId

    fun findMemberById(
        targetMemberId: MemberId,
        currentMemberId: MemberId,
    ): ProfileResponse

    // ToDo 로직 연결 후 추후 제거
    fun findMemberByIdMock(targetMemberId: MemberId): ProfileResponse

    fun createDefaultMemberRelation(newMemberId: MemberId): List<MemberRelationId>

    fun updateFriendRelation(command: UpdateFriendCommand): MemberRelationId

    fun receivedMySpacePicks(currentMemberId: MemberId): List<PickService.SpacePickDetail>

    fun searchMember(
        memberId: MemberId,
        keyword: String,
    ): List<MemberSearchInfo>

    fun receivedFriendSpacePicks(currentMemberId: MemberId): List<PickService.SpacePickDetail>
}

@Component
@Transactional(readOnly = true)
class DefaultMemberUseCase(
    private val memberService: MemberService,
    private val memberRelationService: MemberRelationService,
    private val coinService: CoinService,
    private val imageService: ImageService,
    private val pickService: PickService,
    private val imageProperties: ProfileImageProperties,
) : MemberUseCase {
    @Transactional
    override fun create(command: MemberUseCase.CreateCommand): MemberId {
        val memberId: MemberId =
            memberService.create(
                MemberService.CreateMember(
                    fullName = command.fullName,
                    profileImageId = command.profileImageId,
                    platform = command.platform,
                    ordinal = command.ordinal,
                    gender = command.gender
                )
            )

        // 가입 시, coin 정보 생성
        coinService.create(memberId)
        return memberId
    }

    @Transactional
    override fun update(command: MemberUseCase.UpdateCommand): MemberId {
        return memberService.update(
            MemberService.UpdateMember(
                memberId = command.memberId,
                profileImageId = command.profileImageId
            )
        )
    }

    override fun findMemberById(
        targetMemberId: MemberId,
        currentMemberId: MemberId,
    ): MemberUseCase.ProfileResponse {
        // ToDo 현재 프로필 조회시 query 5번 호출되는데 나중에 수정할 것인지

        val findMember =
            memberService.findMemberById(targetMemberId)
                ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "NOT EXIST PICKED MEMBER ID $targetMemberId")

        val profileImageId = findMember.profileImageId ?: ImageId("defaultImageUrl")
        val profileImageUrl = (
            imageService.load(profileImageId)?.url
                ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "해당하는 이미지를 찾을 수 없습니다. EmojiImageId: [$profileImageId}]")
        )

        val pickCountByMemberId = pickService.findPickedCountByMemberId(findMember.id)

        val isFriend = memberRelationService.isFriend(currentMemberId, targetMemberId)
        val friendCount = memberRelationService.getFriendRelationIds(targetMemberId).size

        return MemberUseCase.ProfileResponse(
            memberId = findMember.id,
            profileImageUrl = profileImageUrl,
            memberName = findMember.fullName,
            platform = findMember.platform.name,
            ordinal = findMember.ordinal,
            isFriend = isFriend,
            pickCount = pickCountByMemberId,
            friendCount = friendCount
        )
    }

    // ToDo 로직 연결 후 추후 제거
    override fun findMemberByIdMock(targetMemberId: MemberId): MemberUseCase.ProfileResponse {
        return MemberUseCase.ProfileResponse(
            memberId = targetMemberId,
            profileImageUrl = "targetMemberProfileImageUrl",
            memberName = "김아무개",
            platform = MemberPlatform.SPRING.name,
            ordinal = 14,
            isFriend = false,
            pickCount = 0,
            friendCount = 0
        )
    }

    @Transactional
    override fun createDefaultMemberRelation(newMemberId: MemberId): List<MemberRelationId> {
        val allMemberIds =
            memberService.findAllMember()
                .filter { it.id != newMemberId }
                .map { it.id }

        return memberRelationService.bulkCreateRelation(newMemberId, allMemberIds)
    }

    @Transactional
    override fun updateFriendRelation(command: MemberUseCase.UpdateFriendCommand): MemberRelationId {
        return memberRelationService.updateRelationToFriend(command.fromId, command.toId)
    }

    override fun searchMember(
        memberId: MemberId,
        keyword: String,
    ): List<MemberUseCase.MemberSearchInfo> {
        val members = memberService.searchMember(memberId, keyword)

        val relations = memberRelationService.findRelationByIds(memberId, members.map { it.id })
        val relationMap = relations.associateBy { it.toId.value }

        var images = imageService.loadAllByIds(members.map { it.profileImageId })
        var imageMap = images.associateBy { it.id.value }

        return members.map { member ->
            val relation =
                relationMap[member.id.value]?.relation
                    ?: throw DojoException.of(DojoExceptionType.RELATION_NOT_FOUND, "from : ${memberId.value}, to : ${member.id.value}")

            MemberUseCase.MemberSearchInfo(
                memberId = member.id.value,
                profileImageUrl = imageMap[member.profileImageId.value]?.url ?: imageProperties.unknown,
                memberName = member.fullName,
                platform = member.platform.name,
                ordinal = member.ordinal,
                isFriend = RelationType.FRIEND == relation
            )
        }
    }

    override fun receivedMySpacePicks(currentMemberId: MemberId): List<PickService.SpacePickDetail> {
        val mySpacePicks = pickService.getReceivedSpacePicks(currentMemberId)
        return mySpacePicks.calculateRanks()
    }

    override fun receivedFriendSpacePicks(currentMemberId: MemberId): List<PickService.SpacePickDetail> {
        val mySpacePicks = pickService.getReceivedSpacePicks(currentMemberId)
        return mySpacePicks.calculateRanks()
    }
}
