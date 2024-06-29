package com.mashup.dojo.service

import com.mashup.dojo.domain.Question
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionType
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface QuestionService {
    fun getQuestion(questionLongId: QuestionId): Question
}

@Service
class QuestionServiceImpl : QuestionService {
    override fun getQuestion(questionLongId: QuestionId): Question {
        return Question(
            questionLongId,
            "매쉬업에서 술 제일 잘 마실 것 같슨 사람은?",
            QuestionType.FRIEND,
            "image_url",
            LocalDateTime.now(),
            LocalDateTime.now()
        )
    }
}
