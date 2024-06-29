package com.mashup.dojo.dto

import com.mashup.dojo.domain.Candidate

data class SheetResponse(
    val questionSheetResponses: List<SheetSingleResponse>,
)

data class SheetSingleResponse(
    val currentQuestionIndex: Long,
    val totalIndex: Long,
    val question: Question,
    val candidates: List<Candidate>,
)

data class Question(
    val id: String,
    val content: String,
    val imageUrl: String,
    val sheetId: String,
)
