package com.mashup.dojo

import com.mashup.dojo.base.BaseTimeEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalTime

interface PickTimeRepository : JpaRepository<PickTimeEntity, String>, PickTimeQueryRepository

@Entity
@Table(name = "pick_time")
class PickTimeEntity(
    @Id
    val id: String,
    @Column(name = "startTime", nullable = false)
    val startTime: LocalTime,
    @Column(name = "endTime", nullable = false)
    val endTime: LocalTime,
    @Column(name = "active", nullable = false)
    val active: Boolean,
) : BaseTimeEntity()

interface PickTimeQueryRepository {
    fun findAllStartTimes(): List<LocalTime>
}

class PickTimeQueryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : PickTimeQueryRepository {
    override fun findAllStartTimes(): List<LocalTime> {
        val pickTime = QPickTimeEntity.pickTimeEntity

        return jpaQueryFactory
            .select(pickTime.startTime)
            .from(pickTime)
            .where(pickTime.active.isTrue)
            .fetch()
    }
}
