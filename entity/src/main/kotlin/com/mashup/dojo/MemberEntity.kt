package com.mashup.dojo

import com.mashup.dojo.base.BaseTimeEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

private const val DEFAULT_POINT = 200

@Entity
@Table(name = "member")
class MemberEntity(
    @Id
    val id: String,
    @Column(name = "full_name", nullable = false)
    val fullName: String,
    @Column(name = "second_initial_name", nullable = false)
    val secondInitialName: String,
    val profileImageId: String?,
    @Column(name = "platform", nullable = false)
    @Enumerated(EnumType.STRING)
    val platform: Platform,
    val ordinal: Int,
    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    val gender: Gender,
    point: Int = DEFAULT_POINT,
    @OneToMany(mappedBy = "picker", cascade = [CascadeType.ALL], orphanRemoval = true)
    val pickers: MutableList<PickEntity> = mutableListOf(),
    @OneToMany(mappedBy = "picked", cascade = [CascadeType.ALL], orphanRemoval = true)
    val picked: MutableList<PickEntity> = mutableListOf(),
) : BaseTimeEntity() {
    @Column(name = "point", nullable = false)
    var point: Int = point
        protected set

    fun updatePoint(newPoint: Int) {
        this.point = newPoint
    }

    companion object {
        fun createMemberEntity(
            id: String,
            fullName: String,
            secondInitialName: String,
            profileImageId: String?,
            platform: Platform,
            gender: Gender,
            ordinal: Int,
        ): MemberEntity {
            return MemberEntity(
                id = id,
                fullName = fullName,
                secondInitialName = secondInitialName,
                profileImageId = profileImageId,
                platform = platform,
                ordinal = ordinal,
                gender = gender,
                DEFAULT_POINT
            )
        }
    }
}

enum class Platform {
    PRODUCT_DESIGN,
    WEB,
    IOS,
    ANDROID,
    SPRING,
}

enum class Gender {
    MALE,
    FEMALE,
}
