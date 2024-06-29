package com.mashup.dojo.service

import com.mashup.dojo.domain.Question
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionType
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface QuestionService {
    fun getQuestion(id: QuestionId): Question
}

@Service
class DefaultQuestionService : QuestionService {
    override fun getQuestion(id: QuestionId): Question {
        // todo : questionid 를 통해 Question 불러오기 
        return getMockQuestionSet(id)
    }

    companion object {
        fun getMockQuestionSet(id: QuestionId): Question {
            return Question(
                id,
                "매쉬업에서 술 제일 잘 마실 것 같슨 사람은?",
                QuestionType.FRIEND,
                "image_url",
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        }
    }
}
