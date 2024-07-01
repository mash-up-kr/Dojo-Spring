package com.mashup.dojo

import com.mashup.dojo.base.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<MemberEntity, String>

@Entity
class MemberEntity(
    @Id
    val id: String,
    val fullName: String,
    val secondInitialName: String,
    val profileImageId: String?,
    val platform: String,
    val ordinal: Int,
    val gender: String,
    val point: Int,
) : BaseEntity()
