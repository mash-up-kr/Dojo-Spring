package com.mashup.dojo.dto

import com.mashup.dojo.domain.Candidate

data class SheetResponse(
    val questionSheetResponses: List<SheetSingleResponse>,
)

data class SheetSingleResponse(
    val questionSheetId: Long,
    val currentQuestionIndex: Long,
    val question: Question,
    val candidates: List<Candidate>,
)

data class Question(
    val id: Long,
    val content: String,
    val imageUrl: String
)
