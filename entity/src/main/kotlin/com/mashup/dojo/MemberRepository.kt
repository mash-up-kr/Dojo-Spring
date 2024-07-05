package com.mashup.dojo

import com.mashup.dojo.member.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<MemberEntity, String>
