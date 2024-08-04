package com.mashup.dojo.usecase

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.Candidate
import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.MemberPlatform
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
        val currentQuestionSet = questionService.getLatestPublishedQuestionSet()
        return questionService.createQuestionSet(excludedQuestionSet = currentQuestionSet)
    }

    @Transactional
    override fun createCustomQuestionSet(command: QuestionUseCase.CreateQuestionSetCommand): QuestionSet {
        return questionService.createQuestionSet(command.questionIdList, command.publishedAt)
    }

    @Transactional
    override fun createQuestionSheet(): List<QuestionSheet> {
        val currentQuestionSet = questionService.getNextOperatingQuestionSet() ?: throw DojoException.of(DojoExceptionType.QUESTION_SET_NOT_READY)
        val allMemberRecords = memberService.findAllMember()
        return questionService.createQuestionSheets(currentQuestionSet, allMemberRecords)
    }

    override fun getQuestionSheetList(memberId: MemberId): QuestionUseCase.GetQuestionSheetsResult {
        return TEMP_GET_QUESTION_SHEETS_RESULT
        // 운영중인 questionSet 조회 (todo : scheduler 가 최신 QuestionSet 을 발행 시각 2분전에 publishedYn Y 로 변경 예정)
        // todo : qSet /
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
            startingQuestionIndex = operatingQSet.questionIds.size - questionSheetResults.size,
            questionSheetList = questionSheetResults
        )
    }

    companion object {
        private val TEMP_CANDIDATES_LIST =
            listOf(
                QuestionUseCase.QuestionSheetCandidateResult(
                    candidateId = MemberId("1"),
                    memberName = "낭은영",
                    platform = MemberPlatform.DESIGN.name
                ),
                QuestionUseCase.QuestionSheetCandidateResult(
                    candidateId = MemberId("2"),
                    memberName = "오시연",
                    platform = MemberPlatform.DESIGN.name
                ),
                QuestionUseCase.QuestionSheetCandidateResult(
                    candidateId = MemberId("3"),
                    memberName = "김준형",
                    platform = MemberPlatform.SPRING.name
                ),
                QuestionUseCase.QuestionSheetCandidateResult(
                    candidateId = MemberId("4"),
                    memberName = "오예원",
                    platform = MemberPlatform.SPRING.name
                ),
                QuestionUseCase.QuestionSheetCandidateResult(
                    candidateId = MemberId("5"),
                    memberName = "박세원",
                    platform = MemberPlatform.SPRING.name
                ),
                QuestionUseCase.QuestionSheetCandidateResult(
                    candidateId = MemberId("6"),
                    memberName = "최민석",
                    platform = MemberPlatform.WEB.name
                ),
                QuestionUseCase.QuestionSheetCandidateResult(
                    candidateId = MemberId("7"),
                    memberName = "이현재",
                    platform = MemberPlatform.WEB.name
                ),
                QuestionUseCase.QuestionSheetCandidateResult(
                    candidateId = MemberId("8"),
                    memberName = "황태규",
                    platform = MemberPlatform.WEB.name
                )
            )

        private val TEMP_QUESTION_SHEET_RESULT =
            listOf(
                QuestionUseCase.QuestionSheetResult(
                    questionSheetId = QuestionSheetId("1"),
                    resolverId = MemberId("temp"),
                    questionId = QuestionId("1"),
                    questionOrder = 1,
                    questionContent = "연애하면 잘해줄 것 같은 사람",
                    questionCategory = "DATING",
                    questionEmojiImageUrl = "https://dojo-backend-source-bundle.s3.ap-northeast-2.amazonaws.com/love.gif",
                    candidates = TEMP_CANDIDATES_LIST
                ),
                QuestionUseCase.QuestionSheetResult(
                    questionSheetId = QuestionSheetId("2"),
                    resolverId = MemberId("temp"),
                    questionId = QuestionId("2"),
                    questionOrder = 2,
                    questionContent = "밥 한끼 사주고 싶은 사람",
                    questionCategory = QuestionCategory.FRIENDSHIP.name,
                    questionEmojiImageUrl = "https://dojo-backend-source-bundle.s3.ap-northeast-2.amazonaws.com/waiting.gif",
                    candidates = TEMP_CANDIDATES_LIST
                ),
                QuestionUseCase.QuestionSheetResult(
                    questionSheetId = QuestionSheetId("3"),
                    resolverId = MemberId("temp"),
                    questionId = QuestionId("3"),
                    questionOrder = 3,
                    questionContent = "인스타 피드 염탐하고 싶은 사람",
                    questionCategory = QuestionCategory.OTHER.name,
                    questionEmojiImageUrl = "https://dojo-backend-source-bundle.s3.ap-northeast-2.amazonaws.com/etc.gif",
                    candidates = TEMP_CANDIDATES_LIST
                ),
                QuestionUseCase.QuestionSheetResult(
                    questionSheetId = QuestionSheetId("4"),
                    resolverId = MemberId("temp"),
                    questionId = QuestionId("4"),
                    questionOrder = 4,
                    questionContent = "헤르미온느 일정 소화할 것 같은 사람",
                    questionCategory = QuestionCategory.FITNESS.name,
                    questionEmojiImageUrl = "https://dojo-backend-source-bundle.s3.ap-northeast-2.amazonaws.com/health.gif",
                    candidates = TEMP_CANDIDATES_LIST
                ),
                QuestionUseCase.QuestionSheetResult(
                    questionSheetId = QuestionSheetId("5"),
                    resolverId = MemberId("temp"),
                    questionId = QuestionId("5"),
                    questionOrder = 5,
                    questionContent = "혼자 틱톡 찍어봤을 것 같은 사람",
                    questionCategory = QuestionCategory.OTHER.name,
                    questionEmojiImageUrl = "https://dojo-backend-source-bundle.s3.ap-northeast-2.amazonaws.com/joke.gif",
                    candidates = TEMP_CANDIDATES_LIST
                ),
                QuestionUseCase.QuestionSheetResult(
                    questionSheetId = QuestionSheetId("6"),
                    resolverId = MemberId("temp"),
                    questionId = QuestionId("6"),
                    questionOrder = 6,
                    questionContent = "밥 먹을 때 쩝쩝 소리 안낼 것 같은 사람",
                    questionCategory = QuestionCategory.PERSONALITY.name,
                    questionEmojiImageUrl = "https://dojo-backend-source-bundle.s3.ap-northeast-2.amazonaws.com/personality.gif",
                    candidates = TEMP_CANDIDATES_LIST
                ),
                QuestionUseCase.QuestionSheetResult(
                    questionSheetId = QuestionSheetId("7"),
                    resolverId = MemberId("temp"),
                    questionId = QuestionId("7"),
                    questionOrder = 7,
                    questionContent = "내가 얘보다는 나을 거 같은 사람",
                    questionCategory = QuestionCategory.HUMOR.name,
                    questionEmojiImageUrl = "https://dojo-backend-source-bundle.s3.ap-northeast-2.amazonaws.com/intimacy.gif",
                    candidates = TEMP_CANDIDATES_LIST
                ),
                QuestionUseCase.QuestionSheetResult(
                    questionSheetId = QuestionSheetId("8"),
                    resolverId = MemberId("temp"),
                    questionId = QuestionId("8"),
                    questionOrder = 7,
                    questionContent = "메이플하다가 현피뜨러 서울역 갈 것 같은 사람",
                    questionCategory = QuestionCategory.FITNESS.name,
                    questionEmojiImageUrl = "https://dojo-backend-source-bundle.s3.ap-northeast-2.amazonaws.com/health.gif",
                    candidates = TEMP_CANDIDATES_LIST
                ),
                QuestionUseCase.QuestionSheetResult(
                    questionSheetId = QuestionSheetId("9"),
                    resolverId = MemberId("temp"),
                    questionId = QuestionId("9"),
                    questionOrder = 9,
                    questionContent = "아침으로 해독주스 만들어 먹을 것 같은 사람",
                    questionCategory = QuestionCategory.FITNESS.name,
                    questionEmojiImageUrl = "https://dojo-backend-source-bundle.s3.ap-northeast-2.amazonaws.com/personality.gif",
                    candidates = TEMP_CANDIDATES_LIST
                ),
                QuestionUseCase.QuestionSheetResult(
                    questionSheetId = QuestionSheetId("10"),
                    resolverId = MemberId("temp"),
                    questionId = QuestionId("10"),
                    questionOrder = 10,
                    questionContent = "핸드폰 앨범에 셀카가 제일 많을 것 같은 사람",
                    questionCategory = QuestionCategory.APPEARANCE.name,
                    questionEmojiImageUrl = "https://dojo-backend-source-bundle.s3.ap-northeast-2.amazonaws.com/appearence.gif",
                    candidates = TEMP_CANDIDATES_LIST
                ),
                QuestionUseCase.QuestionSheetResult(
                    questionSheetId = QuestionSheetId("11"),
                    resolverId = MemberId("temp"),
                    questionId = QuestionId("11"),
                    questionOrder = 11,
                    questionContent = "재테크 배워보고 싶은 사람",
                    questionCategory = QuestionCategory.WORK.name,
                    questionEmojiImageUrl = "https://dojo-backend-source-bundle.s3.ap-northeast-2.amazonaws.com/work.gif",
                    candidates = TEMP_CANDIDATES_LIST
                ),
                QuestionUseCase.QuestionSheetResult(
                    questionSheetId = QuestionSheetId("11"),
                    resolverId = MemberId("temp"),
                    questionId = QuestionId("11"),
                    questionOrder = 11,
                    questionContent = "엉덩이로 이름 잘 쓸 것 같은 사람",
                    questionCategory = QuestionCategory.HUMOR.name,
                    questionEmojiImageUrl = "https://dojo-backend-source-bundle.s3.ap-northeast-2.amazonaws.com/congrat.gif",
                    candidates = TEMP_CANDIDATES_LIST
                )
            )
        val TEMP_GET_QUESTION_SHEETS_RESULT =
            QuestionUseCase.GetQuestionSheetsResult(
                resolverId = MemberId("temp"),
                questionSetId = QuestionSetId("1"),
                sheetTotalCount = 12,
                startingQuestionIndex = 1,
                questionSheetList = TEMP_QUESTION_SHEET_RESULT
            )
    }
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
