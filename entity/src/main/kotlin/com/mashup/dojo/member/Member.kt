package com.mashup.dojo.member

import com.mashup.dojo.base.BaseTimeEntity
import com.mashup.dojo.profile.Profile
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "member")
open class Member protected constructor(
    @Column(name = "name", nullable = false)
    val name: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "part", nullable = false)
    val part: Part,
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    val gender: Gender,
    point: Int = 200,
    @Column(name = "generation", nullable = false)
    val generation: Int,
    @OneToOne(mappedBy = "member")
    val profile: Profile? = null,
) : BaseTimeEntity() {
    @Column(name = "point", nullable = false)
    var point: Int = point
        private set

    fun updatePoint(newPoint: Int) {
        this.point = newPoint
    }

    companion object {
        fun createMember(
            name: String,
            part: Part,
            gender: Gender,
            generation: Int,
        ): Member {
            return Member(name, part, gender, 200, generation)
        }
    }
}
