package com.mashup.dojo

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<MemberEntity, String>
