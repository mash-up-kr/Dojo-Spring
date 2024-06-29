package com.mashup.dojo.usecase

import com.mashup.dojo.domain.Candidate
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionSetId
import com.mashup.dojo.service.MemberService
import com.mashup.dojo.service.PickService
import com.mashup.dojo.service.QuestionService
import com.mashup.dojo.usecase.SheetUseCase.QuestionSheet
import org.springframework.stereotype.Component
import java.time.LocalDateTime

interface SheetUseCase {
    data class QuestionSheet(
        val questionSetId: Long,
        val questionId: Long,
        val imageUrl: String,
        val members: List<Candidate>
    )

    fun getCurrentQuestion(): QuestionSheet
}

@Component
class DefaultSheetUseCase(
    private val pickService: PickService,
    private val memberService: MemberService,
    private val questionService: QuestionService
) : SheetUseCase {
    override fun getCurrentQuestion(): QuestionSheet {

        // 스케줄러로 저장된 것에서 현재 질문 세트를 갖고온다고 가정
        val questionSet = pickService.initData()

        val now = LocalDateTime.now()
        val currentMemberId = MemberId(1L)

        /*
        todo QuestionSet - QuestionSheet => 시간과 currentMemberId로 본인이 안한 질문 추출
        아래는 뺐다고 가정 
         */

        // 추출했다고 가정
        // Mock Data
        val questionSetId = QuestionSetId(questionSet.id.value)
        val questionId = QuestionId(questionSet.questionIds[0].value)
        val question = questionService.getQuestion(questionId)

        /*
        해당 질문의 타입이 친구인지, 전체인지 내부에서 조회 후
        전체 -> 전체 중 랜덤 4명
        친구 -> 친구 중 랜덤 4명
         */

        val friendsIds = memberService.findFriendsIds(currentMemberId)

        val useCaseMemberResponses: List<Candidate> = friendsIds.map { friend ->
            val findMember = memberService.findMemberById(friend.memberId)
            Candidate(memberId = findMember.id, memberName = findMember.fullName, findMember.ordinal)
        }

        return QuestionSheet(
            questionId = question.id.value,
            questionSetId = questionSetId.value,
            imageUrl = question.imageUrl,
            members = useCaseMemberResponses
        )
    }
}
