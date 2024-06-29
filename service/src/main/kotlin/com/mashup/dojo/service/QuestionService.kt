package com.mashup.dojo.service

import com.mashup.dojo.domain.Question
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionType
import io.github.oshai.kotlinlogging.KotlinLogging

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class QuestionService {

    @Transactional
    fun createQuestion(content: String, type: QuestionType, imageUrl: String): Question {
        // todo create questionEntity 
        // return QuestionRepository.save(content, type, imageUrl).toQuestion
        val question = sampleQuestion

        log.info { "Create Question Success : $question" }

        return sampleQuestion
    }

    companion object {
        val sampleQuestion = Question(
            id = QuestionId(8181818),
            content = "세상에서 제일 멋쟁이인 사람",
            type = QuestionType.FRIEND,
            imageUrl = "default:url",
            createdAt = LocalDateTime.now(),
            deletedAt = null,
        )
    }
}
