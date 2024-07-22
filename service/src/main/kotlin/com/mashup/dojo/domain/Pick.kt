package com.mashup.dojo.domain

import com.mashup.dojo.UUIDGenerator
import java.time.LocalDateTime

@JvmInline
value class PickId(val value: String)

data class Pick(
    val id: PickId,
    val questionId: QuestionId,
    // 고른 멤버
    val pickerId: MemberId,
    // 골라진 멤버
    val pickedId: MemberId,
    // 성별 공개 여부
    val isGenderOpen: Boolean = false,
    // 플랫폼 공개 여부
    val isPlatformOpen: Boolean = false,
    // 이름 중간 글자 공개 여부
    val isMidInitialNameOpen: Boolean = false,
    // 이름 전체 공개 여부
    val isFullNameOpen: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        internal fun create(
            questionId: QuestionId,
            pickerId: MemberId,
            pickedId: MemberId,
        ): Pick {
            val uuid = UUIDGenerator.generate()

            return Pick(
                id = PickId(uuid),
                questionId = questionId,
                pickerId = pickerId,
                pickedId = pickedId,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }

    internal fun isOpened(pickOpenItem: PickOpenItem): Boolean {
        return when {
            PickOpenItem.GENDER.value == pickOpenItem.value -> isGenderOpen
            PickOpenItem.PLATFORM.value == pickOpenItem.value -> isPlatformOpen
            PickOpenItem.MID_INITIAL_NAME.value == pickOpenItem.value -> isMidInitialNameOpen
            PickOpenItem.FULL_NAME.value == pickOpenItem.value -> isFullNameOpen
            else -> throw IllegalArgumentException()
        }
    }

    internal fun open(pickOpenItem: PickOpenItem): Pick {
        return when {
            PickOpenItem.GENDER.value == pickOpenItem.value -> copy(isGenderOpen = true)
            PickOpenItem.PLATFORM.value == pickOpenItem.value -> copy(isPlatformOpen = true)
            PickOpenItem.MID_INITIAL_NAME.value == pickOpenItem.value -> copy(isMidInitialNameOpen = true)
            PickOpenItem.FULL_NAME.value == pickOpenItem.value -> copy(isFullNameOpen = true)
            else -> throw IllegalArgumentException()
        }
    }

    internal fun getOpenItem(
        pickOpenItem: PickOpenItem,
        picker: Member,
    ): String {
        return when {
            PickOpenItem.GENDER.value == pickOpenItem.value -> picker.gender.name
            PickOpenItem.PLATFORM.value == pickOpenItem.value -> picker.platform.name
            PickOpenItem.MID_INITIAL_NAME.value == pickOpenItem.value -> picker.secondInitialName
            PickOpenItem.FULL_NAME.value == pickOpenItem.value -> picker.fullName
            else -> throw IllegalArgumentException()
        }
    }
}

enum class PickOpenItem(val value: String, val cost: Int) {
    GENDER("성별", 10),
    PLATFORM("플랫폼", 50),
    MID_INITIAL_NAME("초성 1자 (중간 이름)", 50),
    FULL_NAME("이름", 150),
}

enum class PickSort {
    LATEST,
    MOST_PICKED,
}
