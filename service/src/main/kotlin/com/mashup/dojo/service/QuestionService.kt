package com.mashup.dojo.service

import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.Question
import com.mashup.dojo.domain.QuestionCategory
import com.mashup.dojo.domain.QuestionId
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
                    QuestionId(1),
                    QuestionId(2),
                    QuestionId(3),
                    QuestionId(4),
                    QuestionId(5),
                    QuestionId(6),
                    QuestionId(7),
                    QuestionId(8),
                    QuestionId(9),
                    QuestionId(10),
                    QuestionId(11),
                    QuestionId(12)
                ),
                publishedAt = LocalDateTime.now(),
            )
    }
}
