package com.mashup.dojo

import com.mashup.dojo.base.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class SampleEntity(
    @Id
    val id: String,
    val name: String,
) : BaseEntity()
