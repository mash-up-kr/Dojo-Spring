package com.mashup.dojo.usecase

import com.mashup.dojo.UUIDGenerator
import com.mashup.dojo.domain.Candidate
import com.mashup.dojo.domain.Image
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.QuestionSheetId
import com.mashup.dojo.service.DefaultQuestionService
import com.mashup.dojo.service.ImageService
import com.mashup.dojo.service.MemberService
import com.mashup.dojo.usecase.SheetUseCase.UseCaseQuestionSheet
import org.springframework.stereotype.Component

interface SheetUseCase {
    fun createSheet(): List<UseCaseQuestionSheet>

    data class UseCaseQuestionSheet(
        val questionSheetId: String,
        val currentQuestionIndex: Int,
        val questionId: String,
        val questionContent: String,
        val questionEmojiImageUrl: String,
        val candidates: List<Candidate>,
    )
}

@Component
class DefaultSheetUseCase(
    private val memberService: MemberService,
    private val questionService: DefaultQuestionService,
    private val imageService: ImageService,
) : SheetUseCase {
    override fun createSheet(): List<UseCaseQuestionSheet> {
        // 스케줄러에서 생성된 질문 set 갖고옴. Mock
        val currentQuestionSet = questionService.getCurrentQuestionSet()

        // 유저 정보를 통해 유저 별 질문에 대한 후보자를 선정함. 
        questionService.createQuestionSheet()
        val questionSheetId = QuestionSheetId(UUIDGenerator.generate())
        
        currentQuestionSet?.let { qSet -> 
            // todo : add get User Info -> userId
            val currMemberId = MemberId(MOCK_MEMBER_ID)
            
            qSet.questionOrders.map { 
                val question = questionService.getQuestion(it.questionId)
                val type = question.type

                val candidates = memberService.getCandidates(currMemberId, type)
                // todo : get ImageUrl by imageId -> imageService.getImageUrl(question.emojiImageId)
                
                // questionService or QuestionSheetService.createQuestionSheet(currentQuestionSet, )
                UseCaseQuestionSheet(
                    questionSheetId = questionSheetId,
                    currentQuestionIndex = it.order,
                    questionId = question.id.value,
                    questionContent = question.content,
                    questionEmojiImageUrl = Image.MOCK_IMAGE.url,
                    candidates = candidates
                )
                candidates
                
            }

        } ?: questionService.createQuestionSet(EMPTY_QUESTION_SET)
        val mockEmojiImage = Image.MOCK_IMAGE


        val currentMemberId = MemberId("1")

        // 후보자 4명 찾는 로직, 현재 Mock, 추후에 아래의 것을 스케줄러에서 생성한 것으로 대치
        val candidates = memberService.getCandidates(currentMemberId)

      

        val questionSheets = questionSet.questionOrders.map {
            // todo : question Id -> question 정보 조회 

            UseCaseQuestionSheet(
                questionSheetId = questionSheetId.value,
                currentQuestionIndex = it.order,
                questionId = it.questionId.value,
                questionContent = "여기서 술을 제일 잘 먹을 것 같은 사람은?",
                questionEmojiImageUrl = mockEmojiImage.url,
                candidates = candidates
            )
        }.toList()

        return questionSheets
    }

    companion object {
        private const val MOCK_MEMBER_ID = "1asdasdadsa"
        private val EMPTY_QUESTION_SET = null
    }

}
