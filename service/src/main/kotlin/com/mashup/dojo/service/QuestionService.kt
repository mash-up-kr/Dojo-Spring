package com.mashup.dojo.service

import com.mashup.dojo.domain.Candidate
import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.Member
import com.mashup.dojo.domain.MemberId
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

interface QuestionService {
    fun createQuestion(
        content: String,
        type: QuestionType,
        emojiImageId: ImageId,
    ): Question

    fun getCurrentQuestionSet(): QuestionSet?

    fun createQuestionSet(excludedQuestionSet: QuestionSet?): QuestionSet

    fun createQuestionSet(
        questionIds: List<QuestionId>,
        publishedAt: LocalDateTime,
    ): QuestionSet


    fun createQuestionSheets(
        questionSet: QuestionSet?,
        members: List<Member>,
    ): List<QuestionSheet>

    fun getQuestionById(id: QuestionId): Question?
}

@Service
@Transactional(readOnly = true)
class DefaultQuestionService : QuestionService {
    @Transactional
    override fun createQuestion(
        content: String,
        type: QuestionType,
        emojiImageId: ImageId,
    ): Question {
        // todo create questionEntity
        // return QuestionRepository.save(content, type, imageUrl).toQuestion
        val question = SAMPLE_QUESTION

        log.info { "Create Question Success : $question" }

        return SAMPLE_QUESTION
    }

    override fun getCurrentQuestionSet(): QuestionSet? {
        // todo : bring out questionSet in redis
        return SAMPLE_QUESTION_SET
    }

    override fun createQuestionSet(excludedQuestionSet: QuestionSet?): QuestionSet {
        /**
         * todo :
         * - get Question Set (12s) exclude previousQuestionSet
         * - cache put -> QuestionSet and return
         *
         */

        return SAMPLE_QUESTION_SET
    }

    override fun createQuestionSet(
        questionIds: List<QuestionId>,
        publishedAt: LocalDateTime,
    ): QuestionSet {
        require(questionIds.size == DEFAULT_QUESTION_SIZE) { "questions size for QuestionSet must be 12" }
        require(publishedAt >= LocalDateTime.now()) { "publishedAt must be in the future" }

        val questionOrders = questionIds.mapIndexed { idx, qId -> QuestionOrder(qId, idx + 1) }
        // todo : getId by UUID String Generator
        // questionSetRepository.save()
        return SAMPLE_QUESTION_SET
    }


    override fun createQuestionSheets(
        questionSet: QuestionSet?,
        members: List<Member>,
    ): List<QuestionSheet> {
        /**
         * TODO:
         * target : members
         * question : QuestionSet
         * candidate : member.candidate()
         *
         * - make friend logic, get Candidate logic
         * - cache put -> QuestionSet and return
         * - Temporarily set to create for all members, discuss details later
         */
        return LIST_SAMPLE_QUESTION_SHEET
    }

    override fun getQuestionById(id: QuestionId): Question? {
        // TODO("Not yet implemented")
        return SAMPLE_QUESTION
    }

    companion object {
        private const val DEFAULT_QUESTION_SIZE: Int = 12
        val SAMPLE_QUESTION =
            Question(
                id = QuestionId("1234564"),
                content = "세상에서 제일 멋쟁이인 사람",
                type = QuestionType.FRIEND,
                category = QuestionCategory.DATING,
                emojiImageId = ImageId("345678"),
                createdAt = LocalDateTime.now(),
                deletedAt = null
            )

        val SAMPLE_QUESTION_SET =
            QuestionSet(
                id = QuestionSetId("1"),
                questionIds =
                    listOf(
                        QuestionOrder(QuestionId("1"), 1),
                        QuestionOrder(QuestionId("2"), 2),
                        QuestionOrder(QuestionId("3"), 3),
                        QuestionOrder(QuestionId("4"), 4),
                        QuestionOrder(QuestionId("5"), 5),
                        QuestionOrder(QuestionId("6"), 6),
                        QuestionOrder(QuestionId("7"), 7),
                        QuestionOrder(QuestionId("8"), 8),
                        QuestionOrder(QuestionId("9"), 9),
                        QuestionOrder(QuestionId("10"), 10),
                        QuestionOrder(QuestionId("11"), 11),
                        QuestionOrder(QuestionId("12"), 12)
                    ),
                publishedAt = LocalDateTime.now()
            )

        private val SAMPLE_QUESTION_SHEET =
            QuestionSheet(
                questionSheetId = QuestionSheetId("1"),
                questionSetId = SAMPLE_QUESTION_SET.id,
                questionId = QuestionId("1"),
                resolverId = MemberId("1"),
                candidates =
                    listOf(
                        Candidate(MemberId("2"), "임준형", 1),
                        Candidate(MemberId("3"), "한씨", 1),
                        Candidate(MemberId("4"), "박씨", 1),
                        Candidate(MemberId("5"), "오씨", 1)
                    )
            )

        // TODO: Set to 3 sheets initially. Need to modify for all users later.
        val LIST_SAMPLE_QUESTION_SHEET =
            listOf(SAMPLE_QUESTION_SHEET, SAMPLE_QUESTION_SHEET, SAMPLE_QUESTION_SHEET)
    }
}
