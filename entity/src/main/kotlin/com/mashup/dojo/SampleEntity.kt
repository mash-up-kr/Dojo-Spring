package com.mashup.dojo

import com.mashup.dojo.base.BaseEntity
import jakarta.persistence.Entity

@Entity
class SampleEntity(
    val name: String,
) : BaseEntity()
