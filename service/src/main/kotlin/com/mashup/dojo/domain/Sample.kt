package com.mashup.dojo.domain

import java.time.LocalDateTime

data class Sample(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isDeleted: Boolean,
)
