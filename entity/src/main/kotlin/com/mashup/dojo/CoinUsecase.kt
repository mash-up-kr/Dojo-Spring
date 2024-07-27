package com.mashup.dojo

import com.mashup.dojo.base.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository

interface CoinUseDetailRepository : JpaRepository<CoinUseDetailEntity, String>

@Entity
@Table("coin_use_detail")
class CoinUseDetailEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,
    @Column(name = "coin_id", nullable = false)
    val coinId: String,
    @Column(name = "usage_status", nullable = false)
    @Enumerated(EnumType.STRING)
    val usageStatus: UsageStatus,
    @Column(name = "cost", nullable = false)
    val cost: Long,
    @Column(name = "detail", nullable = false)
    val detail: String,
) : BaseTimeEntity()

enum class UsageStatus {
    USED,
    EARNED,
}
