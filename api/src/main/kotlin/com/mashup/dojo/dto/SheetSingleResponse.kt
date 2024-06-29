package com.mashup.dojo.dto

import com.mashup.dojo.domain.Candidate

data class SheetResponse(
    val questionSheetResponses: List<SheetSingleResponse>,
)

data class SheetSingleResponse(
    val questionSheetId: Long,
    val currentQuestionIndex: Long,
    val questionId: Long,
    val questionContent: String,
    val imageUrl: String,
    val candidates: List<Candidate>,
)
