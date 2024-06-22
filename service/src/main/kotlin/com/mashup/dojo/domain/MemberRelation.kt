package com.mashup.dojo.domain

/**
 * 멤버와 멤버간 관계
 */
data class MemberRelation(
    val from: MemberId,
    val to: MemberId,
    val relation: RelationType = RelationType.ACCOMPANY,
) {
    fun changeRelation(relation: RelationType): MemberRelation {
        return this.copy(relation = relation)
    }
}

enum class RelationType {
    FRIEND,
    ACCOMPANY,
}
