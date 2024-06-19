package com.mashup.dojo.member

import com.mashup.dojo.base.BaseTimeEntity
import com.mashup.dojo.pick.Pick
import com.mashup.dojo.profile.Profile
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "member")
class Member(
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
    @OneToMany(mappedBy = "fromMember", cascade = [CascadeType.ALL], orphanRemoval = true)
    val fromPicks: MutableList<Pick> = mutableListOf(),
    @OneToMany(mappedBy = "toMember", cascade = [CascadeType.ALL], orphanRemoval = true)
    val toPicks: MutableList<Pick> = mutableListOf(),
) : BaseTimeEntity() {
    @Column(name = "point", nullable = false)
    var point: Int = point
        protected set

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
