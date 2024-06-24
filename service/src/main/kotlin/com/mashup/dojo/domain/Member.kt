package com.mashup.dojo.domain

import java.time.LocalDateTime

/**
 * 멤버
 * (Mashup)Member
 */
@JvmInline
value class MemberId(val value: Long)

data class Member(
    val id: MemberId,
    val fullName: String,
    val secondInitialName: String,
    val profileImageUrl: String,
    val platform: MemberPlatform,
    val ordinal: Int,
    val gender: MemberGender,
    val point: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
) {
    fun changeProfileImage(profileImageUrl: String): Member {
        return this.copy(profileImageUrl = profileImageUrl, updatedAt = LocalDateTime.now())
    }

    fun earnPoint(point: Int): Member {
        return this.copy(point = this.point + point, updatedAt = LocalDateTime.now())
    }

    fun spendPoint(point: Int): Member {
        // Todo Exception 객체 쓰기
        if (this.point < point) throw IllegalArgumentException("포인트가 부족합니다.")
        return this.copy(point = this.point - point, updatedAt = LocalDateTime.now())
    }
}

enum class MemberGender {
    MALE,
    FEMALE,
}

enum class MemberPlatform {
    SPRING,
    WEB,
    NODE,
    ANDROID,
    IOS,
    DESIGN,
}
