package com.mashup.dojo.usecase

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.QuestionCategory
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionSet
import com.mashup.dojo.domain.QuestionSetId
import com.mashup.dojo.domain.QuestionSheet
import com.mashup.dojo.domain.QuestionSheetId
import com.mashup.dojo.domain.QuestionType
import com.mashup.dojo.service.ImageService
import com.mashup.dojo.service.MemberRelationService
import com.mashup.dojo.service.MemberService
import com.mashup.dojo.service.PickService
import com.mashup.dojo.service.QuestionService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

interface QuestionUseCase {
    data class CreateCommand(
        val content: String,
        val type: QuestionType,
        val category: QuestionCategory,
        val emojiImageId: ImageId,
    )

    data class CreateQuestionSetCommand(
        val questionIdList: List<QuestionId>,
        val publishedAt: LocalDateTime,
        val endAt: LocalDateTime,
    )

    data class GetQuestionSheetsResult(
        val resolverId: MemberId,
        val questionSetId: QuestionSetId,
        // 문제지 내 문제 수 (1based)
        val sheetTotalCount: Int,
        // 유저가 풀어야하는 질문지 순서 (1based)
        val startingQuestionIndex: Int,
        val questionSheetList: List<QuestionSheetResult>,
    )

    data class QuestionSheetResult(
        val questionSheetId: QuestionSheetId,
        val resolverId: MemberId,
        val questionId: QuestionId,
        val questionOrder: Int,
        val questionContent: String,
        val questionCategory: String,
        val questionEmojiImageUrl: String,
        val candidates: List<QuestionSheetCandidateResult>,
    )

    data class QuestionSheetCandidateResult(
        val candidateId: MemberId,
        val memberName: String,
        val memberImageUrl: String,
        val platform: String,
    )

    fun create(command: CreateCommand): QuestionId

    fun bulkCreate(commands: List<CreateCommand>): List<QuestionId>

    fun createQuestionSet(): QuestionSetId

    fun createCustomQuestionSet(command: CreateQuestionSetCommand): QuestionSet

    fun createQuestionSheet(): List<QuestionSheet>

    fun getQuestionSheetList(memberId: MemberId): GetQuestionSheetsResult
}

@Component
@Transactional(readOnly = true)
class DefaultQuestionUseCase(
    private val questionService: QuestionService,
    private val memberService: MemberService,
    private val pickService: PickService,
    private val imageService: ImageService,
    private val memberRelationService: MemberRelationService,
) : QuestionUseCase {
    @Transactional
    override fun create(command: QuestionUseCase.CreateCommand): QuestionId {
        return questionService.createQuestion(
            command.content,
            command.type,
            command.category,
            command.emojiImageId
        )
    }

    @Transactional
    override fun bulkCreate(commands: List<QuestionUseCase.CreateCommand>): List<QuestionId> {
        return commands.map {
            questionService.createQuestion(it.content, it.type, it.category, it.emojiImageId)
        }
    }

    @Transactional
    override fun createQuestionSet(): QuestionSetId {
        // 가장 마지막에 만들어진 QSet 정보는 제외
        val latestQSet = questionService.getLatestPublishedQuestionSet()

        return questionService.createQuestionSet(latestQuestionSet = latestQSet)
    }

    @Transactional
    override fun createCustomQuestionSet(command: QuestionUseCase.CreateQuestionSetCommand): QuestionSet {
        return questionService.createQuestionSet(command.questionIdList, command.publishedAt, command.endAt)
    }

    @Transactional
    override fun createQuestionSheet(): List<QuestionSheet> {
        val currentQuestionSet =
            questionService.getNextOperatingQuestionSet()
                ?: throw DojoException.of(DojoExceptionType.QUESTION_SET_NOT_READY)

        val allMemberRecords = memberService.findAllMember()

        val questions =
            currentQuestionSet.questionIds.map { questionOrder ->
                questionService.getQuestionType(questionOrder.questionId)
            }

        val allMemberQuestionSheets =
            allMemberRecords.flatMap { member ->
                // 각 질문별로 후보자를 선택
                val questionSheets =
                    questions.map { questionOrder ->
                        val candidatesOfFriend = memberRelationService.findCandidateOfFriend(member.id)
                        val candidatesOfAccompany = memberRelationService.findCandidateOfAccompany(member.id)

                        val questionType = questionOrder.type

                        // 질문의 타입에 따라 다른 후보자 리스트를 전달
                        questionService.createQuestionSheetForSingleQuestion(
                            questionSetId = currentQuestionSet.id,
                            questionId = questionOrder.id,
                            questionType = questionType,
                            resolver = member.id,
                            candidatesOfFriend = candidatesOfFriend,
                            candidatesOfAccompany = candidatesOfAccompany
                        )
                    }
                questionSheets
            }

        val questionSheetList = questionService.saveQuestionSheets(allMemberQuestionSheets)
        // QSheet 생성 완료된 QSet 상태 변경
        questionService.updateQuestionSetToReady(currentQuestionSet)
        return questionSheetList
    }

    override fun getQuestionSheetList(memberId: MemberId): QuestionUseCase.GetQuestionSheetsResult {
        val operatingQSet =
            questionService.getOperatingQuestionSet()
                ?: throw DojoException.of(DojoExceptionType.QUESTION_SET_OPERATING_NOT_EXIST)

        val questionIds = operatingQSet.questionIds.map { it.questionId }

        // QuestionSetId & solverId 를 통해 현재 운영중인 QuestionSet 에서 푼 문제가 어디까지 인지 확인
        val solvedQuestionIds =
            pickService.getSolvedPickList(memberId, operatingQSet.id)
                .map { it.questionId }

        // QuestionSheet 조회 후 푼 문제지 필터링
        val questionSheets = questionService.getQuestionSheets(memberId, operatingQSet.id)

        if (questionSheets.isEmpty()) {
            log.error { "questionSheets 가 존재하지 않습니다. memberId : $memberId, questionSetId: ${operatingQSet.id}" }
            throw DojoException.of(DojoExceptionType.QUESTION_SHEET_NOT_EXIST)
        }

        val questionSheetResults =
            questionSheets
                .filterNot { it.questionId in solvedQuestionIds }
                .map { qSheet: QuestionSheet ->
                    // QSheet to QSheetResult
                    val candidateResults: List<QuestionUseCase.QuestionSheetCandidateResult> =
                        qSheet.candidates.map { memberId ->
                            val member = memberService.findMemberById(memberId) ?: throw DojoException.of(DojoExceptionType.MEMBER_NOT_FOUND)
                            val profileImageUrl = imageService.load(member.profileImageId)?.url ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "image id ${member.profileImageId} not exist")

                            QuestionUseCase.QuestionSheetCandidateResult(
                                candidateId = memberId,
                                memberImageUrl = profileImageUrl,
                                memberName = member.fullName,
                                platform = member.platform.name
                            )
                        }

                    val question = questionService.getQuestionById(qSheet.questionId) ?: throw DojoException.of(DojoExceptionType.QUESTION_NOT_EXIST)
                    val imageUrl = imageService.load(question.emojiImageId)?.url ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "image id ${question.emojiImageId} not exist")
                    val questionOrder = questionIds.indexOf(qSheet.questionId)

                    qSheet.toQuestionSheetResult(questionOrder, question.content, question.category, imageUrl, candidateResults)
                }

        return QuestionUseCase.GetQuestionSheetsResult(
            resolverId = memberId,
            questionSetId = operatingQSet.id,
            sheetTotalCount = operatingQSet.questionIds.size,
            // 1based
            startingQuestionIndex = operatingQSet.questionIds.size - questionSheetResults.size + 1,
            questionSheetList = questionSheetResults
        )
    }
}

private fun QuestionSheet.toQuestionSheetResult(
    questionOrder: Int,
    questionContent: String,
    questionCategory: QuestionCategory,
    emojiImageUrl: String,
    candidates: List<QuestionUseCase.QuestionSheetCandidateResult>,
): QuestionUseCase.QuestionSheetResult {
    return QuestionUseCase.QuestionSheetResult(
        questionSheetId = questionSheetId,
        resolverId = resolverId,
        questionId = questionId,
        questionOrder = questionOrder,
        questionContent = questionContent,
        questionCategory = questionCategory.name,
        questionEmojiImageUrl = emojiImageUrl,
        candidates = candidates
    )
}
