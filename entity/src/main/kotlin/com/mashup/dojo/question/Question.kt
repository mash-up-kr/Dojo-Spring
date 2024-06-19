package com.mashup.dojo.question

import com.mashup.dojo.base.BaseTimeEntity
import com.mashup.dojo.pick.Pick
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "question")
open class Question protected constructor(
    @Column(name = "content", nullable = false)
    var content: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "target", nullable = false)
    var target: Target,
    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL], orphanRemoval = true)
    var picks: MutableList<Pick> = mutableListOf(),
) : BaseTimeEntity() {
    companion object {
        fun createQuestion(
            content: String,
            target: Target,
        ): Question {
            return Question(content, target)
        }
    }
}
