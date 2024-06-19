package com.mashup.dojo.notification

import com.mashup.dojo.base.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "notification")
open class Notification protected constructor(
    @Column(name = "title", nullable = false)
    val title: String,
    @Column(name = "content", nullable = false)
    val content: String,
    @Column(name = "topic", nullable = false)
    val topic: String,
    @Column(name = "send_status", nullable = false)
    var sendStatus: Boolean,
) : BaseTimeEntity()
