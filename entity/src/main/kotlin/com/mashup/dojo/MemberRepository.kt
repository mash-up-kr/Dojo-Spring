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

    fun findSingleMemberByFullName(fullName: String): MemberEntity

    fun findSingleMemberByFullNameAndPlatform(
        fullName: String,
        platform: String,
    ): MemberEntity
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

    override fun findSingleMemberByFullName(fullName: String): MemberEntity {
        val member = QMemberEntity.memberEntity

        val results =
            jpaQueryFactory
                .select(member)
                .from(member)
                .where(member.fullName.eq(fullName))
                .fetch()

        when {
            results.size > 1 -> throw DojoException.of(DojoExceptionType.DUPLICATED_MEMBER)
            results.isEmpty() -> throw DojoException.of(DojoExceptionType.MEMBER_NOT_FOUND)
        }

        return results.first()
    }

    override fun findSingleMemberByFullNameAndPlatform(
        fullName: String,
        platform: String,
    ): MemberEntity {
        val member = QMemberEntity.memberEntity

        val results =
            jpaQueryFactory
                .select(member)
                .from(member)
                .where(
                    member.platform.eq(platform),
                    member.fullName.eq(fullName)
                )
                .fetch()

        when {
            results.size > 1 -> throw DojoException.of(DojoExceptionType.DUPLICATED_MEMBER)
            results.isEmpty() -> throw DojoException.of(DojoExceptionType.MEMBER_NOT_FOUND)
        }

        return results.first()
    }
}
