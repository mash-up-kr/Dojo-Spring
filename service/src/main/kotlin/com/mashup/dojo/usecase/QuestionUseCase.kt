package com.mashup.dojo.usecase

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.Candidate
import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.Question
import com.mashup.dojo.domain.QuestionCategory
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionSet
import com.mashup.dojo.domain.QuestionSetId
import com.mashup.dojo.domain.QuestionSheet
import com.mashup.dojo.domain.QuestionSheetId
import com.mashup.dojo.domain.QuestionSheetWithCandidatesId
import com.mashup.dojo.domain.QuestionType
import com.mashup.dojo.service.ImageService
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
        val platform: String,
    )

    fun create(command: CreateCommand): Question

    fun bulkCreate(commands: List<CreateCommand>): List<Question>

    fun createQuestionSet(): QuestionSet

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
) : QuestionUseCase {
    override fun create(command: QuestionUseCase.CreateCommand): Question {
        return questionService.createQuestion(
            command.content,
            command.type,
            command.category,
            command.emojiImageId
        )
    }

    @Transactional
    override fun bulkCreate(commands: List<QuestionUseCase.CreateCommand>): List<Question> {
        return commands.map {
            questionService.createQuestion(it.content, it.type, it.category, it.emojiImageId)
        }
    }

    override fun createQuestionSet(): QuestionSet {
        // 직전에 발행된 QuestionSet 확인 및 후보에서 제외 (redis 조회 필요)
        val currentQuestionSet = questionService.getOperatingQuestionSet()
        return questionService.createQuestionSet(currentQuestionSet)
    }

    override fun createCustomQuestionSet(command: QuestionUseCase.CreateQuestionSetCommand): QuestionSet {
        return questionService.createQuestionSet(command.questionIdList, command.publishedAt)
    }

    override fun createQuestionSheet(): List<QuestionSheet> {
        val currentQuestionSet = questionService.getReadyToOperatingQuestionSet() ?: throw DojoException.of(DojoExceptionType.QUESTION_SET_NOT_READY)
        val allMemberRecords = memberService.findAllMember()
        return questionService.createQuestionSheets(currentQuestionSet, allMemberRecords)
    }

    override fun getQuestionSheetList(memberId: MemberId): QuestionUseCase.GetQuestionSheetsResult {
        // 운영중인 questionSet 조회 (todo : scheduler 가 최신 QuestionSet 을 발행 시각 2분전에 publishedYn Y 로 변경 예정)
        val operatingQSet =
            questionService.getOperatingQuestionSet()
                ?: throw DojoException.of(DojoExceptionType.QUESTION_SET_OPERATING_NOT_EXIST)

        val questionIds = operatingQSet.questionIds.map { it.questionId }

        // QuestionSetId & solverId 를 통해 현재 운영중인 QuestionSet 에서 푼 문제가 어디까지 인지 확인
        // todo : Pick 정보에 QuestionSetId 필요함 (QuestionSet 이 없는 경우, 동일 질문 다른 후보자에 대한 정보와 분간 X)
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
                .map { qSheet: QuestionSheetWithCandidatesId ->
                    val candidates =
                        qSheet.candidates.map { memberId ->
                            memberService.findMemberById(memberId)?.let { member ->
                                Candidate(
                                    memberId = member.id,
                                    memberName = member.fullName,
                                    platform = member.platform
                                )
                            } ?: throw DojoException.of(DojoExceptionType.MEMBER_NOT_FOUND)
                        }

                    val questionSheet: QuestionSheet = qSheet.toQuestionSheet(candidates)

                    val question = questionService.getQuestionById(questionSheet.questionId) ?: throw DojoException.of(DojoExceptionType.QUESTION_NOT_EXIST)
                    val imageUrl = imageService.load(question.emojiImageId)?.url ?: throw DojoException.of(DojoExceptionType.NOT_EXIST, "image id ${question.emojiImageId} not exist")
                    val questionOrder = questionIds.indexOf(questionSheet.questionId)

                    questionSheet.toQuestionSheetResult(questionOrder, question.content, question.category, imageUrl)
                }

        return QuestionUseCase.GetQuestionSheetsResult(
            resolverId = memberId,
            questionSetId = operatingQSet.id,
            sheetTotalCount = operatingQSet.questionIds.size,
            startingQuestionIndex = operatingQSet.questionIds.size - questionSheetResults.size + 1,
            questionSheetList = questionSheetResults
        )
    }

    private fun QuestionSheet.toQuestionSheetResult(
        questionOrder: Int,
        questionContent: String,
        questionCategory: QuestionCategory,
        emojiImageUrl: String,
    ): QuestionUseCase.QuestionSheetResult {
        return QuestionUseCase.QuestionSheetResult(
            questionSheetId = questionSheetId,
            resolverId = resolverId,
            questionId = questionId,
            questionOrder = questionOrder,
            questionContent = questionContent,
            questionCategory = questionCategory.name,
            questionEmojiImageUrl = emojiImageUrl,
            candidates = candidates.map { it.toCandidateResult() }
        )
    }

    private fun Candidate.toCandidateResult(): QuestionUseCase.QuestionSheetCandidateResult {
        return QuestionUseCase.QuestionSheetCandidateResult(
            candidateId = memberId,
            memberName = memberName,
            platform = platform.name
        )
    }
}
