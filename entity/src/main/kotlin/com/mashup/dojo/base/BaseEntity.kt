package com.mashup.dojo.base

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity : BaseTimeEntity() {

    @CreatedBy
    @Column(updatable = false)
    var createdBy: String? = null
        private set

    @LastModifiedBy
    var lastModifiedBy: String? = null
        private set

    @Column(nullable = false)
    var deleted: Boolean = false
        private set

    // 재활성화 - soft delete
    fun activate() {
        this.deleted = false
    }

    // 비활성화 - soft delete
    fun deactivate() {
        this.deleted = true
    }
}
