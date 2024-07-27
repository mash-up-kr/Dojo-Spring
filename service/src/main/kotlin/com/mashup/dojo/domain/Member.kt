package com.mashup.dojo.domain

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
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
    // 기수
    val ordinal: Int,
    val gender: MemberGender,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    fun changeProfileImage(profileImageId: ImageId): Member {
        return this.copy(profileImageId = profileImageId, updatedAt = LocalDateTime.now())
    }

    fun update(profileImageId: ImageId?): Member {
        // null 값이면 기존 값 유지해요.
        return copy(
            profileImageId = profileImageId ?: this.profileImageId
        )
    }

    companion object {
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
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }

        internal fun convertToMember(
            id: String,
            fullName: String,
            secondInitialName: String,
            profileImageId: ImageId?,
            ordinal: Int,
            platform: MemberPlatform,
            gender: MemberGender,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
        ): Member {
            return Member(
                id = MemberId(id),
                fullName = fullName,
                secondInitialName = secondInitialName,
                profileImageId = profileImageId,
                ordinal = ordinal,
                platform = platform,
                gender = gender,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

enum class MemberGender {
    MALE,
    FEMALE,
    ;

    companion object {
        fun findByValue(value: String): MemberGender {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw DojoException.of(DojoExceptionType.INVALID_MEMBER_GENDER)
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
        fun findByValue(value: String): MemberPlatform {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw DojoException.of(DojoExceptionType.INVALID_MEMBER_PLATFORM)
        }
    }
}
