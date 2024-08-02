package com.mashup.dojo.domain

import java.time.LocalTime

@JvmInline
value class PickTimeId(val value: String)

data class PickTime(
    val id: PickTimeId,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val active: Boolean,
)
