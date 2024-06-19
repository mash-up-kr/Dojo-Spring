package com.mashup.dojo.profile

import com.mashup.dojo.base.BaseEntity
import com.mashup.dojo.member.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "profile")
open class Profile protected constructor(
    imageUrl: String,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member,
) : BaseEntity() {
    @Column(name = "image_url", nullable = false)
    var imageUrl: String = imageUrl
        private set

    fun updateImageUrl(newImageUrl: String) {
        this.imageUrl = newImageUrl
    }
}
