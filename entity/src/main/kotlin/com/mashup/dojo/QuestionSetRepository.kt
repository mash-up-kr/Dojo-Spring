package com.mashup.dojo

import com.mashup.dojo.base.BaseEntity
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface QuestionSetRepository : JpaRepository<QuestionSetEntity, String> {
    // publishedYn : True && publishedAt > now -> 현재 운영중인 QuestionSet
    fun findFirstByPublishedYnTrueAndPublishedAtAfterOrderByPublishedAt(compareTime: LocalDateTime = LocalDateTime.now()): QuestionSetEntity?

    // publishedYn : True && publishedAt < now -> 발행 직전(예정) QuestionSet
    fun findFirstByPublishedYnTrueAndPublishedAtBeforeOrderByPublishedAt(compareTime: LocalDateTime = LocalDateTime.now()): QuestionSetEntity?
}

@Entity
@Table(name = "question_set")
class QuestionSetEntity(
    @Id
    val id: String,
    @Convert(converter = QuestionIdConverter::class)
    @Column(name = "question_ids", nullable = false)
    val questionIds: List<String>,
    @Column(name = "published_yn", nullable = false)
    val publishedYn: Boolean = false,
    @Column(name = "published_at", nullable = false)
    val publishedAt: LocalDateTime,
) : BaseEntity()

class QuestionIdConverter : AttributeConverter<List<String>, String> {
    override fun convertToDatabaseColumn(attribute: List<String>): String {
        return attribute.joinToString(DELIMITER)
    }

    override fun convertToEntityAttribute(dbData: String): List<String> {
        return dbData.split(DELIMITER).toList()
    }

    companion object {
        private const val DELIMITER = ","
    }
}
