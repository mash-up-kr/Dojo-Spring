package com.mashup.dojo

import com.mashup.dojo.base.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface QuestionRepository : JpaRepository<QuestionEntity, String> {

    @Query("SELECT q FROM QuestionEntity q WHERE q.type = :type AND q.id NOT IN :excludeIds ORDER BY function('RAND')")
    fun findRandomQuestions(
        @Param("type") type: QuestionType,
        @Param("excludeIds") excludeIds: List<String>,
        pageable: Pageable
    ): List<QuestionEntity>
}

@Entity
@Table(name = "question")
class QuestionEntity(
    @Id
    val id: String,
    @Column(name = "content", nullable = false)
    val content: String,
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    val type: QuestionType,
    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    val category: QuestionCategory,
    @Column(name = "imageId", nullable = false)
    val emojiImageId: String,
) : BaseEntity()

enum class QuestionType {
    FRIEND,
    ACCOMPANY,
}

enum class QuestionCategory {
    DATING,
    FRIENDSHIP,
    PERSONALITY,
    ENTERTAINMENT,
    FITNESS,
    APPEARANCE,
    WORK,
    HUMOR,
    OTHER,
}
