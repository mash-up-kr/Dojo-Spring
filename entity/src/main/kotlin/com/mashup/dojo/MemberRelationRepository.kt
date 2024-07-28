package com.mashup.dojo

import com.mashup.dojo.base.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRelationRepository : JpaRepository<MemberRelationEntity, String>

@Entity
@Table(name = "member_relation")
class MemberRelationEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,
    @Column(name = "from_id", nullable = false)
    val fromId: String,
    @Column(name = "to_id", nullable = false)
    val toId: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type", nullable = false)
    val relationType: RelationType,
) : BaseTimeEntity()

enum class RelationType {
    FRIEND,
    ACCOMPANY,
}
