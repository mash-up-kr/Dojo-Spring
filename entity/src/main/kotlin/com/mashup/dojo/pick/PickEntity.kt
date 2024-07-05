package com.mashup.dojo.pick

import com.mashup.dojo.base.BaseTimeEntity
import com.mashup.dojo.member.MemberEntity
import com.mashup.dojo.question.QuestionEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "pick")
class PickEntity(
    @Id
    val id: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    val questionEntity: QuestionEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id")
    val picker: MemberEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id")
    val picked: MemberEntity,
    @Column(name = "is_gender_open")
    val isGenderOpen: Boolean,
    @Column(name = "is_platform_open")
    val isPlatformOpen: Boolean,
    @Column(name = "is_mid_initial_name_open")
    val isMidInitialNameOpen: Boolean,
    @Column(name = "is_full_name_open")
    val isFullNameOpen: Boolean,
) : BaseTimeEntity()
