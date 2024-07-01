package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionSetId
import com.mashup.dojo.dto.QuestionBulkCreateRequest
import com.mashup.dojo.dto.QuestionCreateRequest
import com.mashup.dojo.dto.QuestionSetCustomCreateRequest
import com.mashup.dojo.usecase.QuestionUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Admin", description = "Admin 권한에서 실행할 수 있는 API입니다.")
@RestController
@RequestMapping("/admin")
class AdminController(
    private val questionUseCase: QuestionUseCase,
) {
    // todo : add admin auth param
    @PostMapping("/question")
    @Operation(
        summary = "create Question API",
        description = "질문을 생성합니다(admin용). 성공적으로 생성되면 생성된 질문의 UUID 를 반환합니다.",
        responses = [
            ApiResponse(responseCode = "201", description = "생성된 질문 Id")
        ]
    )
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

    @PostMapping("/question-bulk")
    @Operation(
        summary = "bulk create Question API",
        description = "질문지 bulk 생성(admin용). 성공적으로 생성되면 생성된 질문의 UUID List를 반환합니다.",
        responses = [
            ApiResponse(responseCode = "201", description = "생성된 질문 Id")
        ]
    )
    fun bulkCreateQuestion(
        @Valid @RequestBody request: QuestionBulkCreateRequest,
    ): DojoApiResponse<List<QuestionId>> {
        val createCommands =
            request.questionList.map {
                QuestionUseCase.CreateCommand(it.content, it.type, it.emojiImageId)
            }
        val questionIds =
            questionUseCase.bulkCreate(createCommands)
                .map { it.id }

        return DojoApiResponse.success(questionIds)
    }
    
    @Operation(summary = "create custom QuestionSet API", description = "QuestionSet 자체 생성")
    @PostMapping("/custom-question-set")
    fun createCustomQuestionSet(
        @Valid @RequestBody request: QuestionSetCustomCreateRequest,
    ): DojoApiResponse<QuestionSetId> {
        request.questionIds
        val customQuestionSet =
            questionUseCase.createCustomQuestionSet(
                QuestionUseCase.CreateQuestionSetCommand(request.questionIds, request.publishedAt)
            )

        return DojoApiResponse.success(customQuestionSet.id)
    }
}
