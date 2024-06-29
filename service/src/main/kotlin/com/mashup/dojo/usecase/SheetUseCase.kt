package com.mashup.dojo.usecase

import com.mashup.dojo.domain.Candidate
import com.mashup.dojo.domain.Image
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.Question
import com.mashup.dojo.domain.QuestionCategory
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionSet
import com.mashup.dojo.domain.QuestionSetId
import com.mashup.dojo.domain.QuestionSheet
import com.mashup.dojo.domain.QuestionSheetId
import com.mashup.dojo.domain.QuestionType
import com.mashup.dojo.service.MemberService
import com.mashup.dojo.usecase.SheetUseCase.UseCaseQuestionSheet
import org.springframework.stereotype.Component
import java.time.LocalDateTime

interface SheetUseCase {
    fun generateSheet(): List<UseCaseQuestionSheet>

    data class UseCaseQuestionSheet(
        val questionSheetId: Long,
        val currentQuestionIndex: Long,
        val questionId: Long,
        val questionContent: String,
        val imageUrl: String,
        val candidates: List<Candidate>,
    )
}

@Component
class DefaultSheetUseCase(
    private val memberService: MemberService,
) : SheetUseCase {
    override fun generateSheet(): List<UseCaseQuestionSheet> {
        // 스케줄러에서 생성된 질문 set 갖고옴. Mock
        val mockEmojiImage = Image.MOCK_USER_IMAGE
        val question =
            Question(
                QuestionId(0L),
                "여기서 술을 제일 잘 먹을 것 같은 사람은?",
                QuestionType.FRIEND,
                QuestionCategory.FUN,
                mockEmojiImage.id,
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        val questionIds = mutableListOf<QuestionId>()
        repeat(12) {
            questionIds.add(question.id)
        }

        // Mock
        val questionSet = QuestionSet(QuestionSetId(1L), questionIds, LocalDateTime.now())

        val currentMemberId = MemberId(1L)

        // 후보자 4명 찾는 로직, 현재 Mock, 추후에 아래의 것을 스케줄러에서 생성한 것으로 대치
        val candidates = memberService.getCandidates(currentMemberId)

        val questionSheetId = QuestionSheetId(1L)
        val questionSheet =
            QuestionSheet(
                questionSheetId,
                questionSet.id,
                questionIds[0],
                currentMemberId,
                candidates
            )

        // questionSet 활용하여 몇번째 질문인지 확인하기
        val currentQuestionIndex = 3L

        val questionSheets = mutableListOf<UseCaseQuestionSheet>()
        repeat(12) {
            questionSheets.add(UseCaseQuestionSheet(questionSheetId.value, question.id.value, currentQuestionIndex, question.content, mockEmojiImage.url, candidates))
        }

        return questionSheets
    }
}
