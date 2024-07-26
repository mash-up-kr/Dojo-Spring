package com.mashup.dojo.usecase

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.MemberGender
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.MemberPlatform
import com.mashup.dojo.domain.Pick
import com.mashup.dojo.domain.PickId
import com.mashup.dojo.domain.PickOpenItem
import com.mashup.dojo.domain.PickSort
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.service.ImageService
import com.mashup.dojo.service.MemberService
import com.mashup.dojo.service.PickService
import com.mashup.dojo.service.QuestionService
import com.mashup.dojo.usecase.PickUseCase.GetReceivedPick
import com.mashup.dojo.usecase.PickUseCase.GetReceivedPickListCommand
import com.mashup.dojo.usecase.PickUseCase.OpenPickCommand
import com.mashup.dojo.usecase.PickUseCase.PickOpenInfo
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalTime

interface PickUseCase {
    data class GetReceivedPickListCommand(
        val memberId: MemberId,
        val sort: PickSort,
    )

    data class GetReceivedPick(
        val pickId: PickId,
        val questionId: QuestionId,
        val questionContent: String,
        val questionEmojiImageUrl: String,
        val totalReceivedPickCount: Int,
        val latestPickedAt: LocalDateTime,
    )

    data class GetPagingPickCommand(
        val memberId: MemberId,
        val questionId: QuestionId,
        val pageNumber: Int,
        val pageSize: Int,
    )

    data class GetPagingPick(
        val questionId: QuestionId,
        val questionContent: String,
        val questionEmojiImageUrl: String,
        val totalReceivedPickCount: Int,
        val picks: List<GetReceivedPickDetail>,
        val totalPage: Int,
        val totalElements: Long,
        val isFirst: Boolean,
        val isLast: Boolean,
    )

    data class GetReceivedPickDetail(
        val pickId: PickId,
        val pickerOrdinal: Int,
        val pickerIdOpen: Boolean,
        val pickerId: MemberId,
        val pickerGenderOpen: Boolean,
        val pickerGender: MemberGender,
        val pickerPlatformOpen: Boolean,
        val pickerPlatform: MemberPlatform,
        val pickerSecondInitialNameOpen: Boolean,
        val pickerSecondInitialName: String,
        val pickerFullNameOpen: Boolean,
        val pickerFullName: String,
        val latestPickedAt: LocalDateTime,
    )

    data class CreatePickCommand(
        val questionId: QuestionId,
        val pickerId: MemberId,
        val pickedId: MemberId,
    )

    data class OpenPickCommand(
        val pickId: PickId,
        val pickedId: MemberId,
        val pickOpenItem: PickOpenItem,
    )

    data class PickOpenInfo(
        val pickId: PickId,
        val pickOpenItem: PickOpenItem,
        val value: String,
    )

    fun getReceivedPickList(command: GetReceivedPickListCommand): List<GetReceivedPick>

    fun createPick(command: CreatePickCommand): PickId

    fun getNextPickTime(currentTime: LocalDateTime): LocalDateTime

    fun openPick(openPickCommand: OpenPickCommand): PickOpenInfo

    fun getReceivedPickDetailPaging(command: GetPagingPickCommand): GetPagingPick
}

@Component
class DefaultPickUseCase(
    private val pickService: PickService,
    private val questionService: QuestionService,
    private val imageService: ImageService,
    private val memberService: MemberService,
) : PickUseCase {
    override fun getReceivedPickList(command: GetReceivedPickListCommand): List<GetReceivedPick> {
        val receivedPickList: List<Pick> = pickService.getReceivedPickList(command.memberId, command.sort)

        if (receivedPickList.isEmpty()) return EMPTY_RECEIVED_PICK

        val result =
            receivedPickList.groupBy { it.questionId }
                .flatMap { (questionId, pickList) ->
                    val question =
                        questionService.getQuestionById(questionId)
                            ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "등록되지 않은 QuestionId 입니다. QuestionId: [$questionId]")

                    val url =
                        imageService.load(question.emojiImageId)?.url
                            ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "해당하는 이미지를 찾을 수 없습니다. EmojiImageId: [${question.emojiImageId}]")

                    val pickedTotalCount = pickList.size
                    val latestPickedAt = pickList.maxBy { it.createdAt }.createdAt

                    pickList.map { pick ->
                        GetReceivedPick(
                            pickId = pick.id,
                            questionId = question.id,
                            questionContent = question.content,
                            questionEmojiImageUrl = url,
                            totalReceivedPickCount = pickedTotalCount,
                            latestPickedAt = latestPickedAt
                        )
                    }
                }

        return when (command.sort) {
            PickSort.LATEST -> result.sortedByDescending { it.latestPickedAt }
            PickSort.MOST_PICKED -> result.sortedByDescending { it.totalReceivedPickCount }
        }
    }

    override fun createPick(command: PickUseCase.CreatePickCommand): PickId {
        val question =
            questionService.getQuestionById(command.questionId)
                ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "NOT EXIST QUESTION ID ${command.questionId}")
        val pickedMember =
            memberService.findMemberById(command.pickedId)
                ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "NOT EXIST PICKED MEMBER ID ${command.pickedId}")

        return pickService.create(
            questionId = question.id,
            pickerMemberId = command.pickerId,
            pickedMemberId = pickedMember.id
        )
    }

    override fun openPick(openPickCommand: OpenPickCommand): PickOpenInfo {
        return pickService.openPick(
            openPickCommand.pickId,
            openPickCommand.pickedId,
            openPickCommand.pickOpenItem
        ).let { PickOpenInfo(openPickCommand.pickId, openPickCommand.pickOpenItem, it) }
    }

    override fun getReceivedPickDetailPaging(command: PickUseCase.GetPagingPickCommand): PickUseCase.GetPagingPick {
        val question =
            questionService.getQuestionById(command.questionId)
                ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "등록되지 않은 QuestionId 입니다. QuestionId: [${command.questionId}]")

        val imageUrl =
            imageService.load(question.emojiImageId)?.url
                ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "해당하는 이미지를 찾을 수 없습니다. EmojiImageId: [${question.emojiImageId}]")

        val pickCount: Int = pickService.getPickCount(question.id, command.memberId)

        val pickPaging: Page<Pick> =
            pickService.getPickPaging(question.id, command.memberId, command.pageNumber, command.pageSize)

        val pickDetails =
            pickPaging.content.map { pick ->
                val findMember =
                    memberService.findMemberById(pick.pickerId)
                        ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "해당하는 회원을 찾을 수 없습니다. MemberId: [${pick.pickerId}]")

                val genderOpen = pick.isGenderOpen
                val platformOpen = pick.isPlatformOpen
                val secondInitialNameOpen = pick.isMidInitialNameOpen
                val fullNameOpen = pick.isFullNameOpen
                val pickerIdOpen = fullNameOpen && genderOpen && platformOpen && secondInitialNameOpen

                val pickerId = transformPickerId(pickerIdOpen, findMember.id)
                val pickerGender = transformPickerGender(genderOpen, findMember.gender)
                val pickerPlatform = transformPickerPlatform(platformOpen, findMember.platform)
                val pickerSecondInitialName = transformPickerSecondInitialName(secondInitialNameOpen, findMember.secondInitialName)
                val pickerFullName = transformPickerFullName(fullNameOpen, findMember.fullName)

                PickUseCase.GetReceivedPickDetail(
                    pickId = pick.id,
                    pickerOrdinal = findMember.ordinal,
                    pickerIdOpen = pickerIdOpen,
                    pickerId = pickerId,
                    pickerGenderOpen = genderOpen,
                    pickerGender = pickerGender,
                    pickerPlatformOpen = platformOpen,
                    pickerPlatform = pickerPlatform,
                    pickerSecondInitialNameOpen = secondInitialNameOpen,
                    pickerSecondInitialName = pickerSecondInitialName,
                    pickerFullNameOpen = fullNameOpen,
                    pickerFullName = pickerFullName,
                    latestPickedAt = pick.createdAt
                )
            }

        return PickUseCase.GetPagingPick(
            questionId = question.id,
            questionContent = question.content,
            questionEmojiImageUrl = imageUrl,
            totalReceivedPickCount = pickCount,
            picks = pickDetails,
            totalPage = pickPaging.totalPages,
            totalElements = pickPaging.totalElements,
            isFirst = pickPaging.isFirst,
            isLast = pickPaging.isLast
        )
    }

    fun transformPickerId(
        pickerIdOpen: Boolean,
        pickerId: MemberId,
    ): MemberId {
        return when (pickerIdOpen) {
            true -> pickerId
            false -> MemberId("UNKNOWN")
        }
    }

    fun transformPickerGender(
        pickerGenderOpen: Boolean,
        pickerGender: MemberGender,
    ): MemberGender {
        return when (pickerGenderOpen) {
            true -> pickerGender
            false -> MemberGender.UNKNOWN
        }
    }

    fun transformPickerPlatform(
        pickerPlatformOpen: Boolean,
        pickerPlatform: MemberPlatform,
    ): MemberPlatform {
        return when (pickerPlatformOpen) {
            true -> pickerPlatform
            false -> MemberPlatform.UNKNOWN
        }
    }

    fun transformPickerSecondInitialName(
        pickerSecondInitialNameOpen: Boolean,
        secondInitialName: String,
    ): String {
        return when (pickerSecondInitialNameOpen) {
            true -> secondInitialName
            false -> "UNKNOWN"
        }
    }

    fun transformPickerFullName(
        pickerFullNameOpen: Boolean,
        fullName: String,
    ): String {
        return when (pickerFullNameOpen) {
            true -> fullName
            false -> "UNKNOWN"
        }
    }

    override fun getNextPickTime(currentTime: LocalDateTime): LocalDateTime {
        // fixme: phase1 기준 하루에 2번 투표가 오픈되는데 투표 오픈 시간 값을 어디에 저장해둘지? 하드코딩 or DB 저장
        val pickTimes = listOf(LocalTime.of(9, 0), LocalTime.of(18, 0))

        val today = currentTime.toLocalDate()

        val nextPickTime =
            pickTimes
                .map { today.atTime(it) }
                .firstOrNull { it.isAfter(currentTime) }

        // 다음 투표 시간이 오늘 안에 있다면 반환, 아니면 내일 첫 투표 시간 반환
        return nextPickTime ?: today.plusDays(1).atTime(pickTimes.first())
    }

    companion object {
        private val EMPTY_RECEIVED_PICK = emptyList<GetReceivedPick>()
    }
}
