package com.mashup.dojo

import com.mashup.dojo.usecase.QuestionUseCase
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Question", description = "질문지 관련 API 입니다")
@RequestMapping("/question")
@RestController
class
QuestionController(
    private val questionUseCase: QuestionUseCase,
) {
    @GetMapping
    fun getQuestionSheet(
        // todo add auth for memberId
    ) {
    }
}
