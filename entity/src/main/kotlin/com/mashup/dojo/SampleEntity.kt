package com.mashup.dojo

import jakarta.persistence.Entity

@Entity
class SampleEntity(
    val name: String,
) : BaseEntity()
