package com.mashup.dojo.domain

import com.mashup.dojo.UUIDGenerator
import java.time.LocalDateTime

/**
 * 멤버와 멤버간 관계
 */

@JvmInline
value class MemberRelationId(val value: String)

data class MemberRelation(
    val id: MemberRelationId,
    val fromId: MemberId,
    val toId: MemberId,
    val relation: RelationType = RelationType.ACCOMPANY,
    val lastUpdatedAt: LocalDateTime,
) {
    companion object {
        fun createAccompanyRelation(
            fromId: MemberId,
            toId: MemberId,
        ): MemberRelation {
            return MemberRelation(
                id = MemberRelationId(UUIDGenerator.generate()),
                fromId = fromId,
                toId = toId,
                relation = RelationType.ACCOMPANY,
                lastUpdatedAt = LocalDateTime.now()
            )
        }
    }

    fun updateToFriend(): MemberRelation {
        return this.copy(relation = RelationType.FRIEND, lastUpdatedAt = LocalDateTime.now())
    }

    fun accompany(): MemberRelation {
        return this.copy(relation = RelationType.ACCOMPANY, lastUpdatedAt = LocalDateTime.now())
    }
}

enum class RelationType {
    FRIEND,
    ACCOMPANY,
}
