package com.mashup.dojo.dto

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.MemberGender
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.MemberPlatform
import com.mashup.dojo.domain.PickId
import com.mashup.dojo.domain.PickSort
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionSetId
import com.mashup.dojo.domain.QuestionSheetId
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Schema(description = "Pick 생성 요청")
data class CreatePickRequest(
    @Schema(description = "질문지 id")
    val questionSheetId: QuestionSheetId,
    @Schema(description = "질문 세트 id")
    val questionSetId: QuestionSetId,
    @Schema(description = "질문 id")
    val questionId: QuestionId,
    @Schema(description = "후보자 중 선택한 대상 멤버 id")
    val pickedId: MemberId,
    @Schema(description = "질문 스킵 여부")
    val skip: Boolean,
)

@Schema(description = "Pick 생성 응답")
data class CreatePickResponse(
    @Schema(description = "픽 id")
    val pickId: PickId,
    @Schema(description = "획득한 코인")
    val coin: Int,
)

data class ReceivedPickPagingGetResponse(
    val pickList: List<PickResponse>,
    val totalPage: Int,
    val totalElements: Long,
    val isFirst: Boolean,
    val isLast: Boolean,
    val sort: PickSort,
    val pageNumber: Int,
    val pageSize: Int,
)

// todo : 질문의 유형(카테고리)도 전달해줘야 하는가
data class PickResponse(
    val pickId: PickId,
    val questionId: QuestionId,
    val questionContent: String,
    val questionEmojiImageUrl: String,
    val totalReceivedPickCount: Int,
    val latestPickedAt: LocalDateTime,
)

data class PickDetailPaging(
    val questionId: QuestionId,
    val questionContent: String,
    val questionEmojiImageUrl: String,
    val totalReceivedPickCount: Int,
    val picks: List<ReceivedPickDetail>,
    val totalPage: Int,
    val totalElements: Long,
    val isFirst: Boolean,
    val isLast: Boolean,
)

data class ReceivedPickDetail(
    val pickId: PickId?,
    val pickerProfileImageUrl: String,
    val pickerOrdinal: Int,
    val pickerIdOpen: Boolean,
    val pickerId: PickId?,
    val pickerGenderOpen: Boolean,
    val pickerGender: MemberGender?,
    val pickerPlatformOpen: Boolean,
    val pickerPlatform: MemberPlatform?,
    val pickerSecondInitialNameOpen: Boolean,
    val pickerSecondInitialName: String?,
    val pickerFullNameOpen: Boolean,
    val pickerFullName: String?,
    val latestPickedAt: LocalDateTime,
)

@Schema(description = "픽 오픈 요청")
data class PickOpenRequest(
    @field:NotNull
    val pickOpenItemDto: PickOpenItemDto,
)

@Schema(description = "픽 오픈 응답")
data class PickOpenResponse(
    @Schema(description = "픽 id")
    val pickId: String,
    @Schema(description = "오픈 항목")
    val pickOpenItem: PickOpenItemDto,
    @Schema(description = "오픈된 값")
    val pickOpenValue: String,
    @Schema(description = "이미지 url")
    val pickOpenImageUrl: String,
)

enum class PickOpenItemDto(val value: String) {
    GENDER("성별"),
    PLATFORM("플랫폼"),
    MID_INITIAL_NAME("초성 1자 (중간 이름)"),
    FULL_NAME("이름"),
    ;

    companion object {
        fun findByName(value: String): PickOpenItemDto {
            return PickOpenItemDto.entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw DojoException.of(DojoExceptionType.INVALID_PICK_OPEN_ITEM)
        }
    }
}
