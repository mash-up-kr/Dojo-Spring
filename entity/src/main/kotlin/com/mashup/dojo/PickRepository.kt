package com.mashup.dojo

import com.mashup.dojo.base.BaseTimeEntity
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

interface PickRepository : JpaRepository<PickEntity, String> {
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
    ): Page<PickEntity>

    fun findPickDetailContent(
        memberId: String,
        questionId: String,
        pageable: Pageable,
    ): List<PickEntity>

    fun findPickDetailCount(
        memberId: String,
        questionId: String,
    ): Long
}

class PickRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : PickRepositoryCustom {
    override fun findPickDetailPaging(
        memberId: String,
        questionId: String,
        pageable: Pageable,
    ): Page<PickEntity> {
        val picks = findPickDetailContent(memberId = memberId, questionId = questionId, pageable = pageable)
        val count = findPickDetailCount(memberId = memberId, questionId = questionId)

        return PageImpl(picks, pageable, count)
    }

    override fun findPickDetailContent(
        memberId: String,
        questionId: String,
        pageable: Pageable,
    ): List<PickEntity> {
        val pickEntity = QPickEntity.pickEntity
        return jpaQueryFactory
            .selectFrom(pickEntity)
            .where(
                pickEntity.pickedId.eq(memberId),
                pickEntity.questionId.eq(questionId)
            )
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
            .fetchOne() ?: 0
    }
}
