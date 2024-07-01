package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.dto.QuestionResponse
import com.mashup.dojo.dto.SheetListResponse
import com.mashup.dojo.dto.SheetResponse
import com.mashup.dojo.usecase.SheetUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Sheet", description = "질문지 API")
@RestController
@RequestMapping("/api/sheet")
class SheetController(
    private val sheetUseCase: SheetUseCase,
) {
    @Operation(
        summary = "질문 리스트 목록 조회",
        description = "질문 리스트와 질문에 해당하는 후보자 4명과 현재 질문의 순서, 총 질문의 개수를 반환합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "질문 목록 12개 리스트")
        ]
    )
    @GetMapping
    fun getQuestionSheet(): DojoApiResponse<SheetListResponse> {
        val sheetList = sheetUseCase.createSheet()
        val responses =
            sheetList.map { questionSheet ->
                SheetResponse(
                    currentQuestionIndex = questionSheet.currentQuestionIndex,
                    totalIndex = DEFAULT_TOTAL_INDEX,
                    QuestionResponse(
                        id = questionSheet.questionId,
                        content = questionSheet.questionContent,
                        imageUrl = questionSheet.questionEmojiImageUrl,
                        sheetId = questionSheet.questionSheetId
                    ),
                    candidates = questionSheet.candidates
                )
            }

        return DojoApiResponse.success(SheetListResponse(responses))
    }

    companion object {
        private const val DEFAULT_TOTAL_INDEX: Int = 12
    }
}
