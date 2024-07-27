package com.mashup.dojo

import com.mashup.dojo.base.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.jpa.repository.JpaRepository

interface CoinRepository : JpaRepository<CoinEntity, String> {
    fun findByMemberId(memberId: String): CoinEntity?
}

@Entity
@Table(name = "coin")
class CoinEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,
    @Column(name = "member_id", nullable = false)
    val memberId: String,
    @Column(name = "amount", nullable = false)
    @ColumnDefault("0")
    val amount: Long,
) : BaseTimeEntity()
