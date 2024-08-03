package com.mashup.dojo

import com.mashup.dojo.base.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository

interface PickRepository : JpaRepository<PickEntity, String> {
    fun findAllByPickedId(pickedId: String): List<PickEntity>
}

@Entity
@Table(name = "pick")
class PickEntity(
    @Id
    val id: String,
    @Column(name = "question_id")
    val questionId: String,
    @Column(name = "question_set_id")
    val questionSetId: String,
    @Column(name = "question_sheet_id")
    val questionSheetId: String,
    @Column(name = "picker_id")
    val pickerId: String,
    @Column(name = "picked_id")
    val pickedId: String,
    @Column(name = "is_gender_open")
    val isGenderOpen: Boolean = false,
    @Column(name = "is_platform_open")
    val isPlatformOpen: Boolean = false,
    @Column(name = "is_mid_initial_name_open")
    val isMidInitialNameOpen: Boolean = false,
    @Column(name = "is_full_name_open")
    val isFullNameOpen: Boolean = false,
) : BaseTimeEntity()
