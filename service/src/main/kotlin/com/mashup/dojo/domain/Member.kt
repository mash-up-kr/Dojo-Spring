package com.mashup.dojo.domain

import com.mashup.dojo.UUIDGenerator
import java.time.LocalDateTime

/**
 * 멤버
 * (Mashup)Member
 */
@JvmInline
value class MemberId(val value: String)

data class Member(
    val id: MemberId,
    val fullName: String,
    val secondInitialName: String,
    val profileImageId: ImageId?,
    val platform: MemberPlatform,
    val ordinal: Int, // 기수
    val gender: MemberGender,
    val point: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    fun changeProfileImage(profileImageId: ImageId): Member {
        return this.copy(profileImageId = profileImageId, updatedAt = LocalDateTime.now())
    }

    fun earnPoint(point: Int): Member {
        return this.copy(point = this.point + point, updatedAt = LocalDateTime.now())
    }

    fun spendPoint(point: Int): Member {
        // Todo Exception 객체 쓰기
        if (this.point < point) throw IllegalArgumentException("포인트가 부족합니다.")
        return this.copy(point = this.point - point, updatedAt = LocalDateTime.now())
    }

    companion object {
        private const val MEMBER_INIT_POINT = 200

        internal fun create(
            fullName: String,
            profileImageId: ImageId?,
            platform: MemberPlatform,
            gender: MemberGender,
            ordinal: Int,
        ): Member {
            val uuid = UUIDGenerator.generate()

            // validate fullName length
            if (fullName.length < 2) throw IllegalArgumentException("이름은 2글자 이상이어야해요.")
            val secondInitialName = fullName.substring(1, 2)

            return Member(
                id = MemberId(uuid),
                fullName = fullName,
                secondInitialName = secondInitialName,
                profileImageId = profileImageId,
                platform = platform,
                gender = gender,
                ordinal = ordinal,
                point = MEMBER_INIT_POINT,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
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
