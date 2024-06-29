package com.mashup.dojo.service

import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionSet
import com.mashup.dojo.domain.QuestionSetId
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PickService {
    fun initData(): QuestionSet {
        val questionId1 = QuestionId(1L)
        val questionId2 = QuestionId(1L)
        val questionId3 = QuestionId(1L)
        val questionSetIds = listOf(questionId1, questionId2, questionId3)

        val questionSetId = QuestionSetId(1L)
        return QuestionSet(
            questionSetId, questionSetIds, LocalDateTime.now()
        )
    }
}

