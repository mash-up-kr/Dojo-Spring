package com.mashup.dojo

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionSheetRepository : JpaRepository<QuestionSheet, String>

@Entity
@Table(name = "question_sheet")
class QuestionSheet(
    @Id
    val id: String,
    @Column(name = "question_set_id", nullable = false)
    val questionSetId: String,
    @Column(name = "question_id", nullable = false)
    val questionId: String,
    @Column(name = "resolver_id", nullable = false)
    val resolverId: String,
    @Convert(converter = CandidateConverter::class)
    @Column(name = "candidates", nullable = false)
    val candidates: List<String>
)

class CandidateConverter : AttributeConverter<List<String>, String> {
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

