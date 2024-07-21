package com.mashup.dojo.usecase

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.MemberId
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
import org.springframework.stereotype.Component
import java.time.LocalDateTime

interface PickUseCase {
    data class GetReceivedPickListCommand(
        val memberId: MemberId,
        val sort: PickSort,
    )

    data class GetReceivedPick(
        val questionId: QuestionId,
        val questionContent: String,
        val questionEmojiImageUrl: String,
        val totalReceivedPickCount: Int,
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
        val pickOpenItem: PickOpenItem
    )

    data class PickOpenInfo (
        val pickId: PickId,
        val pickOpenItem: PickOpenItem,
        val value: String
    )
    
    fun getReceivedPickList(command: GetReceivedPickListCommand): List<GetReceivedPick>

    fun createPick(command: CreatePickCommand): PickId
    fun openPick(openPickCommand: OpenPickCommand): PickOpenInfo
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

        val questionId = receivedPickList.first().questionId
        questionService.getQuestionById(questionId)

        val result =
            receivedPickList.groupBy { it.questionId }
                .map { (questionId, pickList) ->
                    val question =
                        questionService.getQuestionById(questionId)
                            ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "등록되지 않은 QuestionId 입니다. QuestionId: [$questionId]")

                    val url =
                        imageService.load(question.emojiImageId)?.url
                            ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "해당하는 이미지를 찾을 수 없습니다. . EmojiImageId: [${question.emojiImageId}]")

                    val pickedTotalCount = pickList.size
                    val latestPickedAt = pickList.maxBy { it.createdAt }.createdAt

                    GetReceivedPick(
                        questionId = question.id,
                        questionContent = question.content,
                        questionEmojiImageUrl = url,
                        totalReceivedPickCount = pickedTotalCount,
                        latestPickedAt = latestPickedAt
                    )
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

    companion object {
        val EMPTY_RECEIVED_PICK = emptyList<GetReceivedPick>()
    }
}
