package com.mashup.dojo

import com.mashup.dojo.base.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class MemberRelationEntity(
    @Id
    val id: String,
    @ManyToOne
    val from: MemberEntity,
    @ManyToOne
    val to: MemberEntity,
    @Enumerated(EnumType.STRING)
    val relationType: RelationType,
) : BaseTimeEntity()

enum class RelationType {
    FRIEND,
    ACCOMPANY,
}
