package com.mashup.dojo.usecase

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.Pick
import com.mashup.dojo.domain.PickSort
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.service.ImageService
import com.mashup.dojo.service.PickService
import com.mashup.dojo.service.QuestionService
import com.mashup.dojo.usecase.PickUseCase.GetReceivedPick
import com.mashup.dojo.usecase.PickUseCase.GetReceivedPickListCommand
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

    fun getReceivedPickList(command: GetReceivedPickListCommand): List<GetReceivedPick>
}

@Component
class DefaultPickUseCase(
    private val pickService: PickService,
    private val questionService: QuestionService,
    private val imageService: ImageService,
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

    companion object {
        val EMPTY_RECEIVED_PICK = emptyList<GetReceivedPick>()
    }
}
