package com.mashup.dojo.domain

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.UUIDGenerator
import java.time.LocalDateTime

@JvmInline
value class PickId(val value: String)

data class Pick(
    val id: PickId,
    val questionId: QuestionId,
    val questionSetId: QuestionSetId,
    val questionSheetId: QuestionSheetId,
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
            questionSetId: QuestionSetId,
            questionSheetId: QuestionSheetId,
            pickerId: MemberId,
            pickedId: MemberId,
        ): Pick {
            val uuid = UUIDGenerator.generate()

            return Pick(
                id = PickId(uuid),
                questionId = questionId,
                questionSetId = questionSetId,
                questionSheetId = questionSheetId,
                pickerId = pickerId,
                pickedId = pickedId,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }

    internal fun isOpened(pickOpenItem: PickOpenItem): Boolean {
        return when (pickOpenItem) {
            PickOpenItem.GENDER -> isGenderOpen
            PickOpenItem.PLATFORM -> isPlatformOpen
            PickOpenItem.MID_INITIAL_NAME -> isMidInitialNameOpen
            PickOpenItem.FULL_NAME -> isFullNameOpen
        }
    }

    internal fun updateOpenItem(pickOpenItem: PickOpenItem): Pick {
        return when (pickOpenItem) {
            PickOpenItem.GENDER -> copy(isGenderOpen = true)
            PickOpenItem.PLATFORM -> copy(isPlatformOpen = true)
            PickOpenItem.MID_INITIAL_NAME -> copy(isMidInitialNameOpen = true)
            PickOpenItem.FULL_NAME ->
                copy(
                    isGenderOpen = true,
                    isPlatformOpen = true,
                    isMidInitialNameOpen = true,
                    isFullNameOpen = true
                )
        }
    }

    internal fun getOpenItem(
        pickOpenItem: PickOpenItem,
        picker: Member,
    ): String {
        return when (pickOpenItem) {
            PickOpenItem.GENDER -> picker.gender.name
            PickOpenItem.PLATFORM -> picker.platform.name
            PickOpenItem.MID_INITIAL_NAME -> picker.secondInitialName
            PickOpenItem.FULL_NAME -> picker.fullName
        }
    }
}

enum class PickOpenItem(val value: String, val cost: Int) {
    GENDER("성별", 10),
    PLATFORM("플랫폼", 50),
    MID_INITIAL_NAME("초성 1자 (중간 이름)", 50),
    FULL_NAME("이름", 150),
    ;

    companion object {
        fun findByName(value: String): PickOpenItem {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw DojoException.of(DojoExceptionType.INVALID_PICK_OPEN_ITEM)
        }
    }
}

enum class PickSort {
    LATEST,
    MOST_PICKED,
    ;

    companion object {
        fun findByValue(value: String): PickSort {
            return PickSort.entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw DojoException.of(DojoExceptionType.SORT_CLIENT_NOT_FOUND)
        }
    }
}
