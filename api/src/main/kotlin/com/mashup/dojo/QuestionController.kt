package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.dto.QuestionCreateRequest
import com.mashup.dojo.usecase.QuestionUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Question", description = "질문지 관련 API 입니다")
@RequestMapping("/question")
@RestController
class QuestionController(
    private val questionUseCase: QuestionUseCase,
) {
    // todo : add auth param
    @Operation(summary = "create Question API", description = "질문지 생성")
    @PostMapping
    fun createQuestion(
        @Valid @RequestBody request: QuestionCreateRequest,
    ): DojoApiResponse<QuestionId> {
        return questionUseCase.create(
            QuestionUseCase.CreateCommand(
                content = request.content,
                type = request.type,
                emojiImageId = request.emojiImageId
            )
        ).let { DojoApiResponse.success(it.id) }
    }
}
