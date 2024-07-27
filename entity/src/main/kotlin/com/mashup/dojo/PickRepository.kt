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
    @Column(name = "questionId")
    val questionId: String,
    @Column(name = "pickerId")
    val pickerId: String,
    @Column(name = "pickedId")
    val pickedId: String,
    @Column(name = "isGenderOpen")
    val isGenderOpen: Boolean = false,
    @Column(name = "isPlatformOpen")
    val isPlatformOpen: Boolean = false,
    @Column(name = "isMidInitialNameOpen")
    val isMidInitialNameOpen: Boolean = false,
    @Column(name = "isFullNameOpen")
    val isFullNameOpen: Boolean = false,
) : BaseTimeEntity()
