package com.mashup.dojo

import com.mashup.dojo.base.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

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
    val isGenderOpen: Boolean,
    @Column(name = "isPlatformOpen")
    val isPlatformOpen: Boolean,
    @Column(name = "isMidInitialNameOpen")
    val isMidInitialNameOpen: Boolean,
    @Column(name = "isFullNameOpen")
    val isFullNameOpen: Boolean,
) : BaseTimeEntity()
