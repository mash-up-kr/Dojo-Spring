package com.mashup.dojo.domain

import com.mashup.dojo.UUIDGenerator
import com.mashup.dojo.member.Gender
import com.mashup.dojo.member.Platform
import java.lang.RuntimeException
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
    // 기수
    val ordinal: Int,
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
    ;

    companion object {
        fun findByValue(value: String): Gender {
            return Gender.entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw RuntimeException("ToDo change to DoJoException")
            // ToDo
            // DojoException.of(DojoExceptionType.INVALID_MEMBER_PLATFORM)
        }
    }
}

enum class MemberPlatform {
    SPRING,
    WEB,
    NODE,
    ANDROID,
    IOS,
    DESIGN,
    ;

    companion object {
        fun findByValue(value: String): Platform {
            return Platform.entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw RuntimeException("ToDo change to DoJoException")
            // ToDo
            // DojoException.of(DojoExceptionType.INVALID_MEMBER_PLATFORM)
        }
    }
}
