package com.mashup.dojo.usecase

import com.mashup.dojo.domain.Question
import com.mashup.dojo.domain.QuestionType
import com.mashup.dojo.service.QuestionService
import org.springframework.stereotype.Component

interface QuestionUseCase {
    data class CreateCommand(
        val content: String,
        val type: QuestionType,
        val imageUrl: String,
    )

    fun create(command: CreateCommand): Question
}

@Component
class QuestionCreateUseCase(
    private val questionService: QuestionService,
) : QuestionUseCase {
    override fun create(command: QuestionUseCase.CreateCommand): Question {
        return questionService.createQuestion(
            command.content,
            command.type,
            command.imageUrl
        )
    }
}
