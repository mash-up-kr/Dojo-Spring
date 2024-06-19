package com.mashup.dojo.pick

import com.mashup.dojo.base.BaseTimeEntity
import com.mashup.dojo.member.Member
import com.mashup.dojo.question.Question
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "pick")
class Pick(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    val question: Question,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id")
    val fromMember: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id")
    val toMember: Member,
) : BaseTimeEntity()
