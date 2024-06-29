package com.mashup.dojo

import com.mashup.dojo.base.BaseEntity
import jakarta.persistence.Entity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository : JpaRepository<ImageEntity, Long>

@Entity
class ImageEntity(
    val uuid: String,
    val url: String,
) : BaseEntity()
