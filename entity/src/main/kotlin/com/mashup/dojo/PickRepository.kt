package com.mashup.dojo

import com.mashup.dojo.base.BaseTimeEntity
import com.querydsl.core.annotations.QueryProjection
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Wildcard
import com.querydsl.jpa.impl.JPAQuery
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

    fun findPickedCountByMemberId(memberId: String): Long

    fun findSolvedPick(
        memberId: String,
        questionSetId: String,
    ): List<PickEntity>

    fun findTopRankPicksByMemberId(
        memberId: String,
        rank: Long,
    ): List<PickQuestionMapper>

    fun findGroupByPickPaging(
        pickedId: String,
        sort: String,
        pageable: Pageable,
    ): Page<PickQuestionDetailMapper>
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
        val imageEntity = QImageEntity.imageEntity

        return jpaQueryFactory
            .select(
                QPickEntityMapper(
                    pickEntity.id,
                    pickEntity.questionId,
                    pickEntity.questionSetId,
                    pickEntity.questionSheetId,
                    pickEntity.pickerId,
                    pickEntity.pickedId,
                    imageEntity.url,
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
            .join(imageEntity).on(memberEntity.profileImageId.eq(imageEntity.id))
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

    private fun isAnyOpen(pickEntity: QPickEntity): BooleanExpression? {
        return pickEntity.isGenderOpen
            .or(pickEntity.isPlatformOpen)
            .or(pickEntity.isMidInitialNameOpen)
            .or(pickEntity.isFullNameOpen)
    }

    override fun findPickedCountByMemberId(memberId: String): Long {
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

    override fun findGroupByPickPaging(
        pickedId: String,
        sort: String,
        pageable: Pageable,
    ): Page<PickQuestionDetailMapper> {
        val content = getGroupByPickContent(pickedId, sort, pageable)
        val totalCount = getGroupByPickTotalCount(pickedId)

        return PageImpl(content, pageable, totalCount)
    }

    private fun getGroupByPickContent(
        pickedId: String,
        sort: String,
        pageable: Pageable,
    ): List<PickQuestionDetailMapper> {
        val pickEntity = QPickEntity.pickEntity
        val questionEntity = QQuestionEntity.questionEntity
        val imageEntity = QImageEntity.imageEntity

        val query =
            jpaQueryFactory
                .select(
                    QPickQuestionDetailMapper(
                        pickEntity.id,
                        questionEntity.id,
                        questionEntity.content,
                        imageEntity.url,
                        // 가장 최근의 Pick 시간 가져오기
                        pickEntity.createdAt.max().`as`("latestPickedAt"),
                        pickEntity.id.count().`as`("totalReceivedPickCount")
                    )
                )
                .from(pickEntity)
                .join(questionEntity).on(pickEntity.questionId.eq(questionEntity.id))
                .join(imageEntity).on(questionEntity.emojiImageId.eq(imageEntity.id))
                .where(pickEntity.pickedId.eq(pickedId))
                // Question, Image URL 기준으로 그룹화
                .groupBy(pickEntity.questionId, imageEntity.url)

        val sortedQuery = getSorted(sort, query)

        return sortedQuery
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .fetch()
    }

    private fun getSorted(
        sort: String,
        query: JPAQuery<PickQuestionDetailMapper>,
    ): JPAQuery<PickQuestionDetailMapper> {
        val pickEntity = QPickEntity.pickEntity
        return when (PickSort.findByValue(sort)) {
            PickSort.MOST_PICKED -> query.orderBy(Wildcard.count.desc(), pickEntity.createdAt.desc())
            PickSort.LATEST -> query.orderBy(pickEntity.createdAt.desc())
        }
    }

    private fun getGroupByPickTotalCount(pickedId: String): Long {
        val pickEntity = QPickEntity.pickEntity

        return jpaQueryFactory
            .select(pickEntity.questionId.countDistinct())
            .from(pickEntity)
            .where(pickEntity.pickedId.eq(pickedId))
            .fetchOne() ?: 0L
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
        val pickerProfileImageUrl: String,
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

enum class PickSort {
    LATEST,
    MOST_PICKED,
    ;

    companion object {
        fun findByValue(value: String): PickSort {
            return PickSort.entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw DojoException.of(DojoExceptionType.SORT_CLIENT_NOT_FOUND)
        }
    }
}

data class PickQuestionDetailMapper
    @QueryProjection
    constructor(
        val pickId: String,
        val questionId: String,
        val questionContent: String,
        val questionEmojiImageUrl: String,
        val latestPickedAt: LocalDateTime,
        val totalReceivedPickCount: Long,
    )
