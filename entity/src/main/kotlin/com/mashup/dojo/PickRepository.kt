package com.mashup.dojo

import com.mashup.dojo.base.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository

interface PickRepository : JpaRepository<PickEntity, String> {
    fun findAllByPickedId(pickedId: String): List<PickEntity>
}

@Entity
class PickEntity(
    @Id
    val id: String,
    val questionId: String,
    val pickerId: String,
    val pickedId: String,
    val isGenderOpen: Boolean = false,
    val isPlatformOpen: Boolean = false,
    val isMidInitialNameOpen: Boolean = false,
    val isFullNameOpen: Boolean = false,
) : BaseTimeEntity()
