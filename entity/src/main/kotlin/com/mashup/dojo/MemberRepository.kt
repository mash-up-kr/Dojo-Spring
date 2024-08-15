package com.mashup.dojo

import com.mashup.dojo.base.BaseEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<MemberEntity, String>, MemberQueryRepository

@Entity
class MemberEntity(
    @Id
    val id: String,
    val fullName: String,
    val secondInitialName: String,
    val profileImageId: String,
    val platform: String,
    val ordinal: Int,
    val gender: String,
) : BaseEntity()

interface MemberQueryRepository {
    fun findByNameContaining(
        memberId: String,
        keyword: String,
    ): List<MemberEntity>
}

class MemberQueryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : MemberQueryRepository {
    override fun findByNameContaining(
        memberId: String,
        keyword: String,
    ): List<MemberEntity> {
        val member = QMemberEntity.memberEntity
        return jpaQueryFactory
            .select(member)
            .from(member)
            .where(member.id.notIn(memberId), member.fullName.contains(keyword))
            .fetch()
    }
}
