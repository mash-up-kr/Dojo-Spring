package com.mashup.dojo.domain

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
    val isGenderOpen: Boolean,
    // 플랫폼 공개 여부
    val isPlatformOpen: Boolean,
    // 이름 중간 글자 공개 여부
    val isMidInitialNameOpen: Boolean,
    // 이름 전체 공개 여부
    val isFullNameOpen: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

enum class PickOpenItem(val value: String, val cost: Int) {
    GENDER("성별", 10),
    PLATFORM("플랫폼", 50),
    MID_INITIAL_NAME("초성 1자 (중간 이름)", 50),
    FULL_NAME("이름", 150),
}
