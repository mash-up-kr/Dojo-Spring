package com.mashup.dojo.base

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0L
    
    @CreatedBy
    @Column(updatable = false)
    var createdBy: String? = null
        private set

    @LastModifiedBy
    var lastModifiedBy: String? = null
        private set

    @Column(nullable = false)
    var isDeleted: Boolean = false
        private set

    // 재활성화 - soft delete
    fun activate() {
        this.isDeleted = false
    }

    // 비활성화 - soft delete
    fun deactivate() {
        this.isDeleted = true
    }
}
