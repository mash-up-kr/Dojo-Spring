package com.mashup.dojo.domain

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.UUIDGenerator
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDateTime

private val logger = KotlinLogging.logger { }

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
    val profileImageId: ImageId = ImageId(DEFAULT_PROFILE_IMAGE_ID),
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
        const val DEFAULT_PROFILE_IMAGE_ID = "profile_default_image_1"

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
            val secondInitialName = InitialParser.parse(fullName.substring(1, 2)[0]) ?: throw IllegalArgumentException("이름은 2글자 이상이어야해요.")

            return if (profileImageId == null) {
                Member(
                    id = MemberId(uuid),
                    fullName = fullName,
                    secondInitialName = secondInitialName,
                    platform = platform,
                    gender = gender,
                    ordinal = ordinal,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            } else {
                Member(
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
        }
    }
}

enum class MemberGender {
    MALE,
    FEMALE,
    UNKNOWN,
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
    UNKNOWN,
    ;

    companion object {
        fun findByValue(value: String): MemberPlatform {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw DojoException.of(DojoExceptionType.INVALID_MEMBER_PLATFORM)
        }
    }
}

object InitialParser {
    private val INITIAL_LIST =
        arrayOf(
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )

    fun parse(input: Char): String? {
        if (input !in ('가'..'힣')) {
            logger.error { "한글이 아닌 경우 파싱이 불가능합니다. input : $input" }
            return null
        }
        val unicode = input.code - 0xAC00
        val index = unicode / (21 * 28)

        return INITIAL_LIST[index].toString()
    }
}
