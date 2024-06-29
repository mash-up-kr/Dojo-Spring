package com.mashup.dojo.dto

import com.mashup.dojo.domain.Candidate

data class SheetResponse(
    val questionSheetResponses: List<SheetSingleResponse>,
)

data class SheetSingleResponse(
    val questionSheetId: String,
    val currentQuestionIndex: Long,
    val totalIndex: Long,
    val question: Question,
    val candidates: List<Candidate>,
) {
    companion object {
        private const val DEFAULT_TOTAL_INDEX: Long = 12L

        fun create(
            questionSheetId: String,
            currentQuestionIndex: Long,
            question: Question,
            candidates: List<Candidate>,
        ): SheetSingleResponse {
            return SheetSingleResponse(
                questionSheetId = questionSheetId,
                currentQuestionIndex = currentQuestionIndex,
                totalIndex = DEFAULT_TOTAL_INDEX,
                question = question,
                candidates = candidates
            )
        }
    }
}

data class Question(
    val id: String,
    val content: String,
    val imageUrl: String,
)
