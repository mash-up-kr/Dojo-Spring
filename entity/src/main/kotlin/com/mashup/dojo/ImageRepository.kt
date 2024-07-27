package com.mashup.dojo

import com.mashup.dojo.base.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository : JpaRepository<ImageEntity, String>

@Entity
@Table(name = "image")
class ImageEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,
    @Column(name = "url", nullable = false)
    val url: String,
) : BaseEntity()
