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
    lateinit var createdBy: String
        protected set

    @LastModifiedBy
    lateinit var lastModifiedBy: String
        protected set

    @Column(nullable = false)
    var isDeleted: Boolean = false
        protected set

    // 재활성화 - soft delete
    fun activate() {
        this.isDeleted = false
    }

    // 비활성화 - soft delete
    fun deactivate() {
        this.isDeleted = true
    }
}
