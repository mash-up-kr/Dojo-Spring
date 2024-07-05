package com.mashup.dojo.question

import com.mashup.dojo.base.BaseTimeEntity
import com.mashup.dojo.pick.PickEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.lang.RuntimeException

@Entity
@Table(name = "question")
class QuestionEntity(
    @Id
    val id: String,
    @Column(name = "content", nullable = false)
    var content: String,
    @Enumerated(EnumType.STRING)
    val type: QuestionType,
    @Enumerated(EnumType.STRING)
    val category: QuestionCategory,
    val emojiImageId: String,
    @OneToMany(mappedBy = "questionEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var pickEntities: MutableList<PickEntity> = mutableListOf(),
) : BaseTimeEntity() {
    companion object {
        fun createQuestion(
            id: String,
            content: String,
            questionTypeString: String,
            categoryString: String,
            emojiImageId: String,
        ): QuestionEntity {
            val questionType = QuestionType.findByValue(questionTypeString)
            val category = QuestionCategory.findByValue(categoryString)

            return QuestionEntity(id, content, questionType, category, emojiImageId)
        }
    }
}

enum class QuestionType {
    FRIEND,
    ACCOMPANY,
    ;

    companion object {
        fun findByValue(value: String): QuestionType {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw RuntimeException("ToDo change to DoJoException")
            // ToDo
            // DojoException.of(DojoExceptionType.INVALID_MEMBER_PLATFORM)
        }
    }
}

enum class QuestionCategory {
    LOVE,
    ENTERTAINMENT, // 유흥
    APPEARANCE,
    FLIRTING, // 작업
    PERSONALITY,
    GET_TO_KNOW,
    JOKE,
    STRENGTH,
    OTHER,
    ;

    companion object {
        fun findByValue(value: String): QuestionCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw RuntimeException("ToDo change to DoJoException")
            // ToDo
            // DojoException.of(DojoExceptionType.INVALID_MEMBER_PLATFORM)
        }
    }
}
