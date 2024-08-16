package com.mashup.dojo

import com.mashup.dojo.base.BaseTimeEntity
import com.querydsl.core.annotations.QueryProjection
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Wildcard
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface PickRepository : JpaRepository<PickEntity, String>, PickRepositoryCustom {
    fun findAllByPickedId(pickedId: String): List<PickEntity>
}

@Entity
@Table(name = "pick")
class PickEntity(
    @Id
    val id: String,
    @Column(name = "question_id")
    val questionId: String,
    @Column(name = "question_set_id")
    val questionSetId: String,
    @Column(name = "question_sheet_id")
    val questionSheetId: String,
    @Column(name = "picker_id")
    val pickerId: String,
    @Column(name = "picked_id")
    val pickedId: String,
    @Column(name = "is_gender_open")
    val isGenderOpen: Boolean = false,
    @Column(name = "is_platform_open")
    val isPlatformOpen: Boolean = false,
    @Column(name = "is_mid_initial_name_open")
    val isMidInitialNameOpen: Boolean = false,
    @Column(name = "is_full_name_open")
    val isFullNameOpen: Boolean = false,
) : BaseTimeEntity()

interface PickRepositoryCustom {
    fun findPickDetailPaging(
        memberId: String,
        questionId: String,
        pageable: Pageable,
    ): Page<PickEntityMapper>

    fun findPickDetailCount(
        memberId: String,
        questionId: String,
    ): Long

    fun getOpenPickerCount(
        questionId: String,
        memberId: String,
    ): Long

    fun findPickCountByMemberId(memberId: String): Long

    fun findSolvedPick(
        memberId: String,
        questionSetId: String,
    ): List<PickEntity>

    fun findTopRankPicksByMemberId(
        memberId: String,
        rank: Long,
    ): List<PickQuestionMapper>
}

class PickRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : PickRepositoryCustom {
    override fun findPickDetailPaging(
        memberId: String,
        questionId: String,
        pageable: Pageable,
    ): Page<PickEntityMapper> {
        val picks = findPickDetailContent(memberId = memberId, questionId = questionId, pageable = pageable)
        val count = findPickDetailCount(memberId = memberId, questionId = questionId)

        return PageImpl(picks, pageable, count)
    }

    private fun findPickDetailContent(
        memberId: String,
        questionId: String,
        pageable: Pageable,
    ): List<PickEntityMapper> {
        val pickEntity = QPickEntity.pickEntity
        val memberEntity = QMemberEntity.memberEntity

        return jpaQueryFactory
            .select(
                QPickEntityMapper(
                    pickEntity.id,
                    pickEntity.questionId,
                    pickEntity.questionSetId,
                    pickEntity.questionSheetId,
                    pickEntity.pickerId,
                    pickEntity.pickedId,
                    pickEntity.isGenderOpen,
                    pickEntity.isPlatformOpen,
                    pickEntity.isMidInitialNameOpen,
                    pickEntity.isFullNameOpen,
                    pickEntity.createdAt,
                    pickEntity.updatedAt,
                    memberEntity.ordinal,
                    memberEntity.gender,
                    memberEntity.platform,
                    memberEntity.secondInitialName,
                    memberEntity.fullName
                )
            )
            .from(pickEntity)
            .join(memberEntity).on(pickEntity.pickerId.eq(memberEntity.id))
            .where(
                pickEntity.pickedId.eq(memberId),
                pickEntity.questionId.eq(questionId)
            )
            .orderBy(pickEntity.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }

    override fun findPickDetailCount(
        memberId: String,
        questionId: String,
    ): Long {
        val pickEntity = QPickEntity.pickEntity
        return jpaQueryFactory
            .select(Wildcard.count)
            .from(pickEntity)
            .where(
                pickEntity.pickedId.eq(memberId),
                pickEntity.questionId.eq(questionId)
            )
            .fetchOne() ?: 0
    }

    override fun getOpenPickerCount(
        questionId: String,
        memberId: String,
    ): Long {
        val pickEntity = QPickEntity.pickEntity
        return jpaQueryFactory
            .select(Wildcard.count)
            .from(pickEntity)
            .where(
                pickEntity.questionId.eq(questionId),
                pickEntity.pickedId.eq(memberId),
                isAnyOpen(pickEntity)
            )
            .fetchOne() ?: 0
    }

    private fun isAnyOpen(pickEntity: QPickEntity): BooleanExpression? {
        return pickEntity.isGenderOpen
            .or(pickEntity.isPlatformOpen)
            .or(pickEntity.isMidInitialNameOpen)
            .or(pickEntity.isFullNameOpen)
    }

    override fun findPickCountByMemberId(memberId: String): Long {
        val pickEntity = QPickEntity.pickEntity
        return jpaQueryFactory
            .select(Wildcard.count)
            .from(pickEntity)
            .where(
                pickEntity.pickedId.eq(memberId)
            )
            .fetchOne() ?: 0
    }

    override fun findSolvedPick(
        memberId: String,
        questionSetId: String,
    ): List<PickEntity> {
        val pickEntity = QPickEntity.pickEntity

        return jpaQueryFactory
            .selectFrom(pickEntity)
            .where(
                pickEntity.questionSetId.eq(questionSetId),
                pickEntity.pickerId.eq(memberId)
            )
            .fetch()
    }

    override fun findTopRankPicksByMemberId(
        memberId: String,
        rank: Long,
    ): List<PickQuestionMapper> {
        val pickEntity = QPickEntity.pickEntity
        val questionEntity = QQuestionEntity.questionEntity

        return jpaQueryFactory
            .select(
                QPickQuestionMapper(
                    pickEntity.id,
                    questionEntity.id,
                    questionEntity.content,
                    pickEntity.createdAt
                )
            )
            .from(pickEntity)
            .join(questionEntity).on(pickEntity.questionId.eq(questionEntity.id))
            .where(pickEntity.pickedId.eq(memberId))
            .groupBy(pickEntity.questionId)
            .orderBy(
                Wildcard.count.desc(),
                pickEntity.createdAt.desc()
            )
            .limit(rank)
            .fetch()
    }
}

data class PickEntityMapper
    @QueryProjection
    constructor(
        val pickId: String,
        val questionId: String,
        val questionSetId: String,
        val questionSheetId: String,
        val pickerId: String,
        val pickedId: String,
        val isGenderOpen: Boolean,
        val isPlatformOpen: Boolean,
        val isMidInitialNameOpen: Boolean,
        val isFullNameOpen: Boolean,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val pickerOrdinal: Int,
        val pickerGender: String,
        val pickerPlatform: String,
        val pickerSecondInitialName: String,
        val pickerFullName: String,
    )

data class PickQuestionMapper
    @QueryProjection
    constructor(
        val pickId: String,
        val questionId: String,
        val questionContent: String,
        val createdAt: LocalDateTime,
    )
