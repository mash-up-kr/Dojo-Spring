package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.dto.QuestionCreateRequest
import com.mashup.dojo.usecase.QuestionUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/question")
@RestController
class QuestionController(
    private val questionUseCase: QuestionUseCase,
) {
    // todo dto validation by @Valid
    @PostMapping
    fun createQuestion(
        @RequestBody request: QuestionCreateRequest,
    ): DojoApiResponse<Unit> {
        questionUseCase.create(
            QuestionUseCase.CreateCommand(
                content = request.content,
                type = request.type,
                imageUrl = request.imageUrl
            )
        )

        return DojoApiResponse.success()
    }
}
