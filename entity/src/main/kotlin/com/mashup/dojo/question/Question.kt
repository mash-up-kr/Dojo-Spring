package com.mashup.dojo.question

import com.mashup.dojo.base.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "question")
open class Question protected constructor(

    @Column(name = "content", nullable = false)
    var content: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "target", nullable = false)
    var target: Target

) : BaseTimeEntity() {

    companion object {
        fun createQuestion(content: String, target: Target): Question {
            return Question(content, target)
        }
    }
}
