package com.mashup.dojo.usecase

import com.mashup.dojo.domain.ImageId
import com.mashup.dojo.domain.Question
import com.mashup.dojo.domain.QuestionSet
import com.mashup.dojo.domain.QuestionType
import com.mashup.dojo.service.QuestionService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

interface QuestionUseCase {
    data class CreateCommand(
        val content: String,
        val type: QuestionType,
        val emojiImageId: ImageId,
    )

    fun create(command: CreateCommand): Question

    fun bulkCreate(commands: List<CreateCommand>): List<Question>

    fun createQuestionSet(): QuestionSet
}

@Component
@Transactional(readOnly = true)
class DefaultQuestionUseCase(
    private val questionService: QuestionService,
) : QuestionUseCase {
    override fun create(command: QuestionUseCase.CreateCommand): Question {
        return questionService.createQuestion(
            command.content,
            command.type,
            command.emojiImageId
        )
    }

    @Transactional
    override fun bulkCreate(commands: List<QuestionUseCase.CreateCommand>): List<Question> {
        return commands.map {
            questionService.createQuestion(it.content, it.type, it.emojiImageId)
        }
    }

    override fun createQuestionSet(): QuestionSet {
        // 직전에 발행된 QuestionSet 확인 및 후보에서 제외 (redis 조회 필요)
        val previousQuestionSet = questionService.getPreviousQuestionSet()
        return questionService.createQuestionSet(previousQuestionSet)
    }
}
