package com.mashup.dojo.dto

import com.mashup.dojo.domain.QuestionType


data class QuestionCreateRequest(
    val content: String,
    val type: QuestionType,
    val imageUrl: String,
)


data class QuestionBulkCreateRequest(
    val questionList: List<QuestionCreateRequest>,
)
