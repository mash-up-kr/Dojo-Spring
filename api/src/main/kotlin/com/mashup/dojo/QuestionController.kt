package com.mashup.dojo

import com.mashup.dojo.config.security.MemberPrincipalContextHolder
import com.mashup.dojo.dto.QuestionSheetCandidate
import com.mashup.dojo.dto.QuestionSheetResponse
import com.mashup.dojo.dto.QuestionSheetsGetResponse
import com.mashup.dojo.usecase.QuestionUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Question", description = "질문지 관련 API 입니다")
@RequestMapping("/question")
@RestController
class QuestionController(
    private val questionUseCase: QuestionUseCase,
) {
    @GetMapping
    @Operation(
        summary = "현재 운영중인 질문지에 대한 투표지 정보 조회",
        description =
            "현재 운영중인 질문지에 대해 질문 및 질문 별 후보자들을 반환합니다." +
                "이미 기존에 동일한 요청에 대해서 투표를 완료했다면, 질문 리스트는 투표를 완료한 수 만큼 제외하여 응답에 제공됩니다." +
                "(기존에 질문세트에 대한 모든 투표를 완료했다면, 질문 리스트는 빈 리스트가 반환됩니다.)",
        responses = [
            ApiResponse(responseCode = "200", description = "OK")
        ]
    )
    fun getQuestionSheet(): QuestionSheetsGetResponse {
        val memberId = MemberPrincipalContextHolder.current().id
        return questionUseCase.getQuestionSheetList(memberId).toResponse()
    }
}

fun QuestionUseCase.GetQuestionSheetsResult.toResponse(): QuestionSheetsGetResponse {
    val questionSheetResponses =
        this.questionSheetList.map { qSheet ->
            val questionSheetCandidates =
                qSheet.candidates.map { candidate ->
                    QuestionSheetCandidate(
                        memberId = candidate.candidateId,
                        memberName = candidate.memberName,
                        memberImageUrl = candidate.memberImageUrl,
                        platform = candidate.platform
                    )
                }.toList()

            QuestionSheetResponse(
                questionSheetId = qSheet.questionSheetId,
                resolverId = qSheet.resolverId,
                questionId = qSheet.questionId,
                questionContent = qSheet.questionContent,
                questionOrder = qSheet.questionOrder,
                questionCategory = qSheet.questionCategory,
                questionEmojiImageUrl = qSheet.questionEmojiImageUrl,
                candidates = questionSheetCandidates
            )
        }.toList()

    return QuestionSheetsGetResponse(
        resolverId = this.resolverId,
        questionSetId = this.questionSetId,
        sheetTotalCount = this.sheetTotalCount,
        startingQuestionIndex = this.startingQuestionIndex,
        questionSheetList = questionSheetResponses
    )
}
