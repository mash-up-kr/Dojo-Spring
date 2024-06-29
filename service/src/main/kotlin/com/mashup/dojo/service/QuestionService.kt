package com.mashup.dojo.service

import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.Question
import com.mashup.dojo.domain.QuestionCategory
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionOrder
import com.mashup.dojo.domain.QuestionSet
import com.mashup.dojo.domain.QuestionSetId
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

    fun createQuestionSet(previousQuestionSet: QuestionSet?): QuestionSet
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

    override fun createQuestionSet(previousQuestionSet: QuestionSet?): QuestionSet {
        /**
         * todo :
         * - get Question Set (12s) exclude previousQuestionSet
         * - cache put -> QuestionSet and return
         *
         */

        return SAMPLE_QUESTION_SET
    }

    companion object {
        val SAMPLE_QUESTION =
            Question(
                id = QuestionId(8181818),
                content = "세상에서 제일 멋쟁이인 사람",
                type = QuestionType.FRIEND,
                category = QuestionCategory.ROMANCE,
                emojiImageId = ImageId(1),
                createdAt = LocalDateTime.now(),
                deletedAt = null
            )

        val SAMPLE_QUESTION_SET =
            QuestionSet(
                id = QuestionSetId(1),
                questionIds = listOf(
                    QuestionOrder(QuestionId(1), 1),
                    QuestionOrder(QuestionId(2), 2),
                    QuestionOrder(QuestionId(3), 3),
                    QuestionOrder(QuestionId(4), 4),
                    QuestionOrder(QuestionId(5), 5),
                    QuestionOrder(QuestionId(6), 6),
                    QuestionOrder(QuestionId(7), 7),
                    QuestionOrder(QuestionId(8), 8),
                    QuestionOrder(QuestionId(9), 9),
                    QuestionOrder(QuestionId(10), 10),
                    QuestionOrder(QuestionId(11), 11),
                    QuestionOrder(QuestionId(12), 12)
                ),
                publishedAt = LocalDateTime.now(),
            )
    }
}
