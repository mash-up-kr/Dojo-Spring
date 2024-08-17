package com.mashup.dojo.service

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.QuestionEntity
import com.mashup.dojo.QuestionRepository
import com.mashup.dojo.QuestionSetEntity
import com.mashup.dojo.QuestionSetRepository
import com.mashup.dojo.QuestionSheetEntity
import com.mashup.dojo.QuestionSheetRepository
import com.mashup.dojo.Status
import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.PublishStatus
import com.mashup.dojo.domain.Question
import com.mashup.dojo.domain.QuestionCategory
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionOrder
import com.mashup.dojo.domain.QuestionSet
import com.mashup.dojo.domain.QuestionSetId
import com.mashup.dojo.domain.QuestionSheet
import com.mashup.dojo.domain.QuestionSheetId
import com.mashup.dojo.domain.QuestionType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.floor

private val log = KotlinLogging.logger {}

interface QuestionService {
    val questionRepository: QuestionRepository
    val questionSetRepository: QuestionSetRepository
    val questionSheetRepository: QuestionSheetRepository

    fun getQuestionById(id: QuestionId): Question?

    fun getOperatingQuestionSet(): QuestionSet?

    fun getNextOperatingQuestionSet(): QuestionSet?

    fun getLatestPublishedQuestionSet(): QuestionSet?

    fun getQuestionSetById(questionSetId: QuestionSetId): QuestionSet?

    fun updateQuestionSetToReady(questionSet: QuestionSet): QuestionSetId

    fun getQuestionSheets(
        resolverId: MemberId,
        questionSetId: QuestionSetId,
    ): List<QuestionSheet>

    fun createQuestion(
        content: String,
        type: QuestionType,
        category: QuestionCategory,
        emojiImageId: ImageId,
    ): QuestionId

    fun createQuestionSet(latestQuestionSet: QuestionSet?): QuestionSetId

    fun createQuestionSet(
        questionIds: List<QuestionId>,
        publishedAt: LocalDateTime,
        endAt: LocalDateTime,
    ): QuestionSet

    fun saveQuestionSheets(allMemberQuestionSheets: List<QuestionSheet>): List<QuestionSheet>

    fun createQuestionSheetForSingleQuestion(
        questionSetId: QuestionSetId,
        questionId: QuestionId,
        resolver: MemberId,
        candidatesOfFriend: List<MemberId>,
        candidatesOfAccompany: List<MemberId>,
        questionType: QuestionType,
    ): QuestionSheet

    fun getQuestionType(questionId: QuestionId): Question
}

@Service
@Transactional(readOnly = true)
class DefaultQuestionService(
    @Value("\${dojo.questionSet.size}")
    private val questionSetSize: Int,
    @Value("\${dojo.questionSet.friend-ratio}")
    private val friendQuestionRatio: Float,
    @Value("\${dojo.questionSet.open-time-1}")
    private val openTime1: LocalTime,
    @Value("\${dojo.questionSet.open-time-2}")
    private val openTime2: LocalTime,
    override val questionRepository: QuestionRepository,
    override val questionSetRepository: QuestionSetRepository,
    override val questionSheetRepository: QuestionSheetRepository,
) : QuestionService {
    @Transactional
    override fun createQuestion(
        content: String,
        type: QuestionType,
        category: QuestionCategory,
        emojiImageId: ImageId,
    ): QuestionId {
        val question =
            Question.create(
                content = content,
                type = type,
                category = category,
                emojiImageId = emojiImageId
            )

        val id = questionRepository.save(question.toEntity()).id

        return QuestionId(id)
    }

    // 현재 운영중인 QuestionSet
    override fun getOperatingQuestionSet(): QuestionSet? {
        return questionSetRepository.findByPublishedAtBeforeAndEndAtAfterOrderByPublishedAtAsc()
            ?.toQuestionSet() ?: run {
            log.error { "Published And Operating QuestionSet Entity not found" }
            null
        }
    }

    // 발행 출격 준비 완료 QuestionSet
    override fun getNextOperatingQuestionSet(): QuestionSet? {
        return questionSetRepository.findFirstByStatusAndPublishedAtAfterOrderByPublishedAtAsc(Status.UPCOMING)?.toQuestionSet() ?: run {
            log.error { "Published And Prepared for sortie QuestionSet Entity not found" }
            null
        }
    }

    override fun getLatestPublishedQuestionSet(): QuestionSet? {
        return questionSetRepository.findTopByOrderByPublishedAtDesc()?.toQuestionSet()
    }

    override fun getQuestionSetById(questionSetId: QuestionSetId): QuestionSet? {
        return questionSetRepository.findByIdOrNull(questionSetId.value)?.toQuestionSet()
    }

    @Transactional
    override fun updateQuestionSetToReady(questionSet: QuestionSet): QuestionSetId {
        val entity = questionSetRepository.save(questionSet.updateToReady().toEntity())
        return QuestionSetId(entity.id)
    }

    override fun getQuestionSheets(
        resolverId: MemberId,
        questionSetId: QuestionSetId,
    ): List<QuestionSheet> {
        return questionSheetRepository.findAllByQuestionSetIdAndResolverId(questionSetId.value, resolverId.value)
            .map { it.toQuestionSheetWithCandidatesId() }
    }

    @Transactional
    override fun createQuestionSet(latestQuestionSet: QuestionSet?): QuestionSetId {
        // 비율에 따라 questionType 선정
        val friendQuestionSize = floor(questionSetSize * friendQuestionRatio).toInt()
        val excludedQuestionIds: List<String> = latestQuestionSet?.questionIds?.map { it.questionId.value } ?: emptyList()

        val friendQuestions =
            questionRepository.findRandomQuestions(com.mashup.dojo.QuestionType.FRIEND, excludedQuestionIds, Pageable.ofSize(friendQuestionSize))
                .map { it.toQuestion() }
        val accompanyQuestions =
            questionRepository.findRandomQuestions(com.mashup.dojo.QuestionType.ACCOMPANY, excludedQuestionIds, Pageable.ofSize(questionSetSize - friendQuestions.size))
                .map { it.toQuestion() }

        val questionList = (friendQuestions + accompanyQuestions).shuffled()

        if (questionList.size != questionSetSize) {
            log.error {
                "QSet 을 만들기 위한 남은 Question 들이 부족합니다. " +
                    "조회한 QuestionSize : ${questionList.size}, 친구용 질문 size : $friendQuestionSize, 전체용 질문 size : ${accompanyQuestions.size}, " +
                    "이전 QSetId : ${latestQuestionSet?.id}, 제외한 QuestionIds : $excludedQuestionIds"
            }

            throw DojoException.of(DojoExceptionType.QUESTION_LACK_FOR_CREATE_QUESTION_SET)
        }

        val questionOrders =
            questionList.mapIndexed { index, question ->
                QuestionOrder(questionId = question.id, order = index)
            }

        // 마지막 QSet 의 발행 시각 가져오기
        val publishedTime =
            latestQuestionSet?.endAt ?: run {
                val now = LocalTime.now()
                val today = LocalDate.now()

                when {
                    now.isBefore(openTime1) -> today.atTime(openTime1)
                    now.isBefore(openTime2) -> today.atTime(openTime2)
                    else -> today.plusDays(1).atTime(openTime1)
                }
            }

        val endTime =
            if (publishedTime.toLocalTime() == openTime1) {
                publishedTime.toLocalDate().atTime(openTime2)
            } else { //  publishedTime.toLocalTime() == PublishedTime.OPEN_TIME_2
                publishedTime.toLocalDate().plusDays(1).atTime(openTime1)
            }

        // 우선 만들어지는 시점이 다음 투표 이전에 만들어질 QSet 을 만든다고 가정, 따라서 해당 QSet 은 바로 다음 발행될 QSet
        val questionSetEntity =
            QuestionSet.create(
                questionOrders = questionOrders,
                publishedAt = publishedTime,
                endAt = endTime
            ).toEntity()

        val id = questionSetRepository.save(questionSetEntity).id

        return QuestionSetId(id)
    }

    @Transactional
    override fun createQuestionSet(
        questionIds: List<QuestionId>,
        publishedAt: LocalDateTime,
        endAt: LocalDateTime,
    ): QuestionSet {
        require(questionIds.size == questionSetSize) { "questions size for QuestionSet must be $questionSetSize" }
        require(publishedAt >= LocalDateTime.now()) { "publishedAt must be in the future" }
        require(endAt > publishedAt) { "endAt must be later than publishedAt " }

        val questionOrders = questionIds.mapIndexed { idx, qId -> QuestionOrder(qId, idx + 1) }
        val questionSet = QuestionSet.create(questionOrders, publishedAt, endAt)

        questionSetRepository.save(questionSet.toEntity())
        return questionSet
    }

    override fun getQuestionType(questionId: QuestionId): Question {
        return questionRepository.findByIdOrNull(questionId.value)?.toQuestion()
            ?: throw DojoException.of(DojoExceptionType.QUESTION_NOT_EXIST)
    }

    @Transactional
    override fun createQuestionSheetForSingleQuestion(
        questionSetId: QuestionSetId,
        questionId: QuestionId,
        resolver: MemberId,
        candidatesOfFriend: List<MemberId>,
        candidatesOfAccompany: List<MemberId>,
        questionType: QuestionType,
    ): QuestionSheet {
        /**
         * ToDo 아래는 추후 캐시에 넣는 작업을 해야합니다.
         * - cache put -> QuestionSet and return
         * - Temporarily set to create for all members, discuss details later
         * 질문의 타입에 따라 적절한 후보자 리스트를 사용
         */

        val candidates =
            when (questionType) {
                QuestionType.FRIEND -> candidatesOfFriend
                QuestionType.ACCOMPANY -> candidatesOfAccompany
            }

        return QuestionSheet.create(
            questionSetId = questionSetId,
            questionId = questionId,
            resolverId = resolver,
            candidates = candidates
        )
    }

    @Transactional
    override fun saveQuestionSheets(allMemberQuestionSheets: List<QuestionSheet>): List<QuestionSheet> {
        val questionSheetEntities = allMemberQuestionSheets.map { it.toEntity() }
        val saveQuestionSheetEntities = questionSheetRepository.saveAll(questionSheetEntities)
        return saveQuestionSheetEntities.map { it.toQuestionSheetWithCandidatesId() }
    }

    override fun getQuestionById(id: QuestionId): Question? {
        return questionRepository.findByIdOrNull(id.value)?.toQuestion()
            ?: throw DojoException.of(DojoExceptionType.QUESTION_NOT_EXIST)
    }
}

private fun Question.toEntity(): QuestionEntity {
    return QuestionEntity(
        id = id.value,
        content = content,
        type =
            when (type) {
                QuestionType.FRIEND -> com.mashup.dojo.QuestionType.FRIEND
                QuestionType.ACCOMPANY -> com.mashup.dojo.QuestionType.ACCOMPANY
            },
        category =
            when (category) {
                QuestionCategory.DATING -> com.mashup.dojo.QuestionCategory.DATING
                QuestionCategory.FRIENDSHIP -> com.mashup.dojo.QuestionCategory.FRIENDSHIP
                QuestionCategory.PERSONALITY -> com.mashup.dojo.QuestionCategory.PERSONALITY
                QuestionCategory.ENTERTAINMENT -> com.mashup.dojo.QuestionCategory.ENTERTAINMENT
                QuestionCategory.FITNESS -> com.mashup.dojo.QuestionCategory.FITNESS
                QuestionCategory.APPEARANCE -> com.mashup.dojo.QuestionCategory.APPEARANCE
                QuestionCategory.WORK -> com.mashup.dojo.QuestionCategory.WORK
                QuestionCategory.HUMOR -> com.mashup.dojo.QuestionCategory.HUMOR
                QuestionCategory.OTHER -> com.mashup.dojo.QuestionCategory.OTHER
            },
        emojiImageId = emojiImageId.value
    )
}

private fun QuestionEntity.toQuestion(): Question {
    return Question(
        id = QuestionId(id),
        content = content,
        type =
            when (type) {
                com.mashup.dojo.QuestionType.FRIEND -> QuestionType.FRIEND
                com.mashup.dojo.QuestionType.ACCOMPANY -> QuestionType.ACCOMPANY
            },
        category =
            when (category) {
                com.mashup.dojo.QuestionCategory.DATING -> QuestionCategory.DATING
                com.mashup.dojo.QuestionCategory.FRIENDSHIP -> QuestionCategory.FRIENDSHIP
                com.mashup.dojo.QuestionCategory.PERSONALITY -> QuestionCategory.PERSONALITY
                com.mashup.dojo.QuestionCategory.ENTERTAINMENT -> QuestionCategory.ENTERTAINMENT
                com.mashup.dojo.QuestionCategory.FITNESS -> QuestionCategory.FITNESS
                com.mashup.dojo.QuestionCategory.APPEARANCE -> QuestionCategory.APPEARANCE
                com.mashup.dojo.QuestionCategory.WORK -> QuestionCategory.WORK
                com.mashup.dojo.QuestionCategory.HUMOR -> QuestionCategory.HUMOR
                com.mashup.dojo.QuestionCategory.OTHER -> QuestionCategory.OTHER
            },
        emojiImageId = ImageId(emojiImageId)
    )
}

private fun QuestionSet.toEntity(): QuestionSetEntity {
    val questionIds = questionIds.map { it.questionId.value }.toList()
    return QuestionSetEntity(
        id = id.value,
        questionIds = questionIds,
        status = status.toDomainPublishStatus(),
        publishedAt = publishedAt,
        endAt = endAt
    )
}

private fun QuestionSetEntity.toQuestionSet(): QuestionSet {
    val questionOrders =
        questionIds.mapIndexed { index, id ->
            QuestionOrder(
                questionId = QuestionId(id),
                order = index
            )
        }.toList()

    return QuestionSet(
        id = QuestionSetId(id),
        questionIds = questionOrders,
        status = status.toDomainPublishStatus(),
        publishedAt = publishedAt,
        endAt = endAt
    )
}

private fun QuestionSheet.toEntity(): QuestionSheetEntity {
    return QuestionSheetEntity(
        id = questionSheetId.value,
        questionSetId = questionSetId.value,
        questionId = questionId.value,
        resolverId = resolverId.value,
        candidates = candidates.map { it.value }.toList()
    )
}

private fun QuestionSheetEntity.toQuestionSheetWithCandidatesId(): QuestionSheet {
    return QuestionSheet(
        questionSheetId = QuestionSheetId(id),
        questionSetId = QuestionSetId(questionSetId),
        questionId = QuestionId(questionId),
        resolverId = MemberId(resolverId),
        candidates = candidates.map { MemberId(it) }.toList()
    )
}

private fun Status.toDomainPublishStatus(): PublishStatus {
    return when (this) {
        Status.TERMINATED -> PublishStatus.TERMINATED
        Status.ACTIVE -> PublishStatus.ACTIVE
        Status.READY -> PublishStatus.READY
        Status.UPCOMING -> PublishStatus.UPCOMING
    }
}

private fun PublishStatus.toDomainPublishStatus(): Status {
    return when (this) {
        PublishStatus.TERMINATED -> Status.TERMINATED
        PublishStatus.ACTIVE -> Status.ACTIVE
        PublishStatus.READY -> Status.READY
        PublishStatus.UPCOMING -> Status.UPCOMING
    }
}
