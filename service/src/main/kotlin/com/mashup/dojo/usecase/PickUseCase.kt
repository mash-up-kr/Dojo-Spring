package com.mashup.dojo.usecase

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.Pick
import com.mashup.dojo.domain.PickId
import com.mashup.dojo.domain.PickOpenItem
import com.mashup.dojo.domain.PickSort
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionSetId
import com.mashup.dojo.domain.QuestionSheetId
import com.mashup.dojo.service.ImageService
import com.mashup.dojo.service.MemberService
import com.mashup.dojo.service.PickService
import com.mashup.dojo.service.QuestionService
import com.mashup.dojo.usecase.PickUseCase.GetReceivedPick
import com.mashup.dojo.usecase.PickUseCase.GetReceivedPickListCommand
import com.mashup.dojo.usecase.PickUseCase.OpenPickCommand
import com.mashup.dojo.usecase.PickUseCase.PickOpenInfo
import org.springframework.stereotype.Component
import java.time.LocalDateTime

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
        val anyOpenPickerCount: Int,
        val picks: List<PickService.GetReceivedPickDetail>,
        val totalPage: Int,
        val totalElements: Long,
        val isFirst: Boolean,
        val isLast: Boolean,
    )

    data class CreatePickCommand(
        val questionSheetId: QuestionSheetId,
        val questionSetId: QuestionSetId,
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

    fun getNextPickTime(): LocalDateTime

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

        val questionSet = questionService.getQuestionSetById(command.questionSetId) ?: throw DojoException.of(DojoExceptionType.QUESTION_SET_NOT_EXIST)

        return pickService.create(
            questionId = question.id,
            questionSetId = questionSet.id,
            questionSheetId = command.questionSheetId,
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

        val receivedPickPaging = pickService.getPickPaging(question.id, command.memberId, command.pageNumber, command.pageSize)

        val anyOpenPickerCount = pickService.getAnyOpenPickerCount(question.id, command.memberId)

        return PickUseCase.GetPagingPick(
            questionId = question.id,
            questionContent = question.content,
            questionEmojiImageUrl = imageUrl,
            totalReceivedPickCount = pickCount,
            anyOpenPickerCount = anyOpenPickerCount,
            picks = receivedPickPaging.picks,
            totalPage = receivedPickPaging.totalPage,
            totalElements = receivedPickPaging.totalElements,
            isFirst = receivedPickPaging.isFirst,
            isLast = receivedPickPaging.isLast
        )
    }

    override fun getNextPickTime(): LocalDateTime {
        return pickService.getNextPickTime()
    }

    companion object {
        private val EMPTY_RECEIVED_PICK = emptyList<GetReceivedPick>()
    }
}
