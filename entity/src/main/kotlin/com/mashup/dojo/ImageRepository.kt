package com.mashup.dojo

import com.mashup.dojo.base.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository : JpaRepository<ImageEntity, String>

@Entity
class ImageEntity(
    @Id
    val id: String,
    val url: String,
) : BaseEntity()
