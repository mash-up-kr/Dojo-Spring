package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.PickId
import com.mashup.dojo.domain.PickOpenItem
import com.mashup.dojo.domain.PickSort
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.dto.CreatePickRequest
import com.mashup.dojo.dto.PickOpenItemDto
import com.mashup.dojo.dto.PickOpenRequest
import com.mashup.dojo.dto.PickOpenResponse
import com.mashup.dojo.dto.PickPaging
import com.mashup.dojo.dto.PickResponse
import com.mashup.dojo.dto.ReceivedPickDetail
import com.mashup.dojo.dto.ReceivedPickListGetResponse
import com.mashup.dojo.usecase.PickUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneId

@Tag(name = "Pick", description = "픽!")
@RestController
@RequestMapping("/pick")
class PickController(
    private val pickUseCase: PickUseCase,
) {
    @GetMapping("/picked-list")
    @Operation(
        summary = "내가 받은 픽 List API",
        description = "내가 받은 픽들을 정렬하여 보여주는 API. default sort : 최신 순",
        responses = [
            ApiResponse(responseCode = "200", description = "내가 받은 픽 리스트")
        ]
    )
    fun getReceivedPickList(
        // todo : add userinfo
        @RequestParam(required = false, defaultValue = "LATEST") sort: PickSort,
    ): DojoApiResponse<ReceivedPickListGetResponse> {
        val receivedPickList: List<PickUseCase.GetReceivedPick> =
            pickUseCase.getReceivedPickList(PickUseCase.GetReceivedPickListCommand(MemberId("1"), sort))

        val pickResponseList =
            receivedPickList.map {
                PickResponse(
                    pickId = it.pickId,
                    questionId = it.questionId,
                    questionContent = it.questionContent,
                    questionEmojiImageUrl = it.questionEmojiImageUrl,
                    totalReceivedPickCount = it.totalReceivedPickCount,
                    latestPickedAt = it.latestPickedAt
                )
            }
        return DojoApiResponse.success(ReceivedPickListGetResponse(pickResponseList, sort))
    }

    @GetMapping("/picked-detail")
    @Operation(
        summary = "내가 받은 픽 중 특정 질문 페이징 API",
        description = "내가 픽 중 특정 질문을 페이징하여 보여주는 API. questionId : 특정 질문의 Id",
        responses = [
            ApiResponse(responseCode = "200", description = "내가 받은 픽 중 특정 질문의 페이징")
        ]
    )
    fun getPickDetail(
        @RequestParam questionId: String,
        @RequestParam(required = false, defaultValue = "0") pageNumber: Int,
        @RequestParam(required = false, defaultValue = "10") pageSize: Int,
    ): DojoApiResponse<PickPaging> {
        val pickPaging: PickUseCase.GetPagingPick =
            pickUseCase.getReceivedPickDetailPaging(PickUseCase.GetPagingPickCommand(MemberId("1"), QuestionId(questionId), pageNumber, pageSize))

        val pickDetails =
            pickPaging.picks.map {
                ReceivedPickDetail(
                    pickId = it.pickId,
                    pickerOrdinal = it.pickerOrdinal,
                    pickerIdOpen = it.pickerIdOpen,
                    pickerId = it.pickerId,
                    pickerGenderOpen = it.pickerGenderOpen,
                    pickerGender = it.pickerGender,
                    pickerPlatformOpen = it.pickerPlatformOpen,
                    pickerPlatform = it.pickerPlatform,
                    pickerSecondInitialNameOpen = it.pickerSecondInitialNameOpen,
                    pickerSecondInitialName = it.pickerSecondInitialName,
                    pickerFullNameOpen = it.pickerFullNameOpen,
                    pickerFullName = it.pickerFullName,
                    latestPickedAt = it.latestPickedAt
                )
            }
        val pickPagingResponse =
            PickPaging(
                questionId = pickPaging.questionId,
                questionContent = pickPaging.questionContent,
                questionEmojiImageUrl = pickPaging.questionEmojiImageUrl,
                totalReceivedPickCount = pickPaging.totalReceivedPickCount,
                picks = pickDetails,
                totalPage = pickPaging.totalPage,
                totalElements = pickPaging.totalElements,
                isFirst = pickPaging.isFirst,
                isLast = pickPaging.isLast
            )

        return DojoApiResponse.success(pickPagingResponse)
    }

    @PostMapping
    @Operation(
        summary = "픽 생성 API",
        description = "질문에 대해 상대방을 선택 시, Pick 정보가 생성됩니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "생성된 pick Id")
        ]
    )
    fun create(
        @RequestBody request: CreatePickRequest,
    ): DojoApiResponse<PickId> {
        // todo : update memberId to AuthInfo
        val pickId = pickUseCase.createPick(PickUseCase.CreatePickCommand(request.questionId, MemberId("1"), request.pickedId))

        return DojoApiResponse.success(pickId)
    }

    @GetMapping("/next-pick-time")
    @Operation(
        summary = "다음 투표 시간 조회 API",
        description = "한국 시간 기준 다음 투표 시간을 반환합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "다음 투표 시간")
        ]
    )
    fun getNextPickTime(): DojoApiResponse<LocalDateTime> {
        val currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val nextPickTime = pickUseCase.getNextPickTime(currentTime)
        return DojoApiResponse.success(nextPickTime)
    }

    @PostMapping("/{id}/open")
    @Operation(
        summary = "내가 받은 픽 정보 오픈 API",
        description = "내가 받은 픽의 정보 중 하나를 오픈하는 API. 픽 오픈 정보 : 성별, 플랫폼, 초성 1자(중간 이름), 이름",
        responses = [
            ApiResponse(responseCode = "200", description = "픽 정보")
        ]
    )
    fun openPick(
        // todo: add userInfo
        @PathVariable id: String,
        @Valid @RequestBody request: PickOpenRequest,
    ): DojoApiResponse<PickOpenResponse> {
        // todd: pickedId에 실제 유저 id 전달
        return pickUseCase.openPick(
            PickUseCase.OpenPickCommand(
                pickId = PickId(id),
                pickedId = MemberId("MOCK_MEMBER_ID"),
                pickOpenItem = PickOpenItem.findByValue(request.pickOpenItemDto.value)
            )
        ).let { DojoApiResponse.success(PickOpenResponse(it.pickId.value, PickOpenItemDto.findByValue(it.pickOpenItem.value), it.value)) }
    }
}
