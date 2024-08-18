package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.config.security.MemberPrincipalContextHolder
import com.mashup.dojo.domain.PickId
import com.mashup.dojo.domain.PickOpenItem
import com.mashup.dojo.domain.PickSort
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.dto.CreatePickRequest
import com.mashup.dojo.dto.PickDetailPaging
import com.mashup.dojo.dto.PickOpenItemDto
import com.mashup.dojo.dto.PickOpenRequest
import com.mashup.dojo.dto.PickOpenResponse
import com.mashup.dojo.dto.PickResponse
import com.mashup.dojo.dto.ReceivedPickDetail
import com.mashup.dojo.dto.ReceivedPickPagingGetResponse
import com.mashup.dojo.usecase.PickUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
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

@Tag(name = "Pick", description = "픽!")
@RestController
@RequestMapping("/pick")
class PickController(
    private val pickUseCase: PickUseCase,
) {
    @GetMapping("/picked-list")
    @Operation(
        summary = "내가 받은 픽 페이징 API",
        description = "내가 받은 픽들을 페이징 처리 후 정렬하여 보여주는 API. default sort : 최신 순",
        responses = [
            ApiResponse(responseCode = "200", description = "내가 받은 픽 리스트 페이징")
        ]
    )
    fun getReceivedPickList(
        @Parameter(
            description = "정렬 기준. LATEST는 최근에 Pick된 항목을 기준으로 정렬하고, MOST_PICKED는 가장 많이 Pick된 항목을 기준으로 정렬합니다.",
            schema = Schema(defaultValue = "LATEST")
        )
        @RequestParam(required = false, defaultValue = "LATEST") sort: String,
        @Parameter(
            description = "페이지 번호. 0부터 시작합니다.",
            schema = Schema(defaultValue = "0")
        )
        @RequestParam(required = false, defaultValue = "0") pageNumber: Int,
        @Parameter(
            description = "페이지 크기. 한 페이지에 포함될 항목의 개수를 설정합니다.",
            schema = Schema(defaultValue = "10")
        )
        @RequestParam(required = false, defaultValue = "10") pageSize: Int,
    ): DojoApiResponse<ReceivedPickPagingGetResponse> {
        val currentMemberId = MemberPrincipalContextHolder.current().id
        val validSort = PickSort.findByValue(sort)

        val receivedPickList =
            pickUseCase.getReceivedPickList(
                PickUseCase.GetReceivedPickPagingCommand(
                    memberId = currentMemberId,
                    sort = validSort,
                    pageNumber = pageNumber,
                    pageSize = pageSize
                )
            )

        val pickResponseList =
            receivedPickList.picks.map {
                PickResponse(
                    pickId = it.pickId,
                    questionId = it.questionId,
                    questionContent = it.questionContent,
                    questionEmojiImageUrl = it.questionEmojiImageUrl,
                    totalReceivedPickCount = it.totalReceivedPickCount,
                    latestPickedAt = it.latestPickedAt
                )
            }

        return DojoApiResponse.success(
            ReceivedPickPagingGetResponse(
                pickList = pickResponseList,
                totalPage = receivedPickList.totalPage,
                totalElements = receivedPickList.totalElements,
                isFirst = receivedPickList.isFirst,
                isLast = receivedPickList.isLast,
                sort = validSort,
                pageNumber = pageNumber,
                pageSize = pageSize
            )
        )
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
    ): DojoApiResponse<PickDetailPaging> {
        val currentMemberId = MemberPrincipalContextHolder.current().id
        val pickDetailPaging: PickUseCase.GetPickDetailPaging =
            pickUseCase.getReceivedPickDetailPaging(PickUseCase.GetPagingPickCommand(currentMemberId, QuestionId(questionId), pageNumber, pageSize))

        val pickDetails =
            pickDetailPaging.picks.map {
                val pickId = if (it.pickId.value == "UNKNOWN") null else it.pickId
                val pickerId = if (it.pickerId.value == "UNKNOWN") null else it.pickId

                ReceivedPickDetail(
                    pickId = pickId,
                    pickerProfileImageUrl = it.pickerProfileImageUrl,
                    pickerOrdinal = it.pickerOrdinal,
                    pickerIdOpen = it.pickerIdOpen,
                    pickerId = pickerId,
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
        val pickDetailPagingResponse =
            PickDetailPaging(
                questionId = pickDetailPaging.questionId,
                questionContent = pickDetailPaging.questionContent,
                questionEmojiImageUrl = pickDetailPaging.questionEmojiImageUrl,
                totalReceivedPickCount = pickDetailPaging.totalReceivedPickCount,
                picks = pickDetails,
                totalPage = pickDetailPaging.totalPage,
                totalElements = pickDetailPaging.totalElements,
                isFirst = pickDetailPaging.isFirst,
                isLast = pickDetailPaging.isLast
            )

        return DojoApiResponse.success(pickDetailPagingResponse)
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
        val currentMemberId = MemberPrincipalContextHolder.current().id
        val pickId =
            pickUseCase.createPick(
                PickUseCase.CreatePickCommand(
                    questionSheetId = request.questionSheetId,
                    questionSetId = request.questionSetId,
                    questionId = request.questionId,
                    pickerId = currentMemberId,
                    pickedId = request.pickedId
                )
            )

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
        val nextPickTime = pickUseCase.getNextPickTime()
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
        @PathVariable id: String,
        @Valid @RequestBody request: PickOpenRequest,
    ): DojoApiResponse<PickOpenResponse> {
        val memberId = MemberPrincipalContextHolder.current().id
        return pickUseCase.openPick(
            PickUseCase.OpenPickCommand(
                pickId = PickId(id),
                pickedId = memberId,
                pickOpenItem = PickOpenItem.findByName(request.pickOpenItemDto.name)
            )
        ).let { DojoApiResponse.success(PickOpenResponse(it.pickId.value, PickOpenItemDto.findByName(it.pickOpenItem.name), it.value)) }
    }
}
