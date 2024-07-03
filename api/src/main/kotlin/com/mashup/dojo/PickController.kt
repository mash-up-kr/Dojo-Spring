package com.mashup.dojo

import com.mashup.dojo.common.DojoApiResponse
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.PickSort
import com.mashup.dojo.dto.PickResponse
import com.mashup.dojo.dto.ReceivedPickListGetResponse
import com.mashup.dojo.usecase.PickUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@Tag(name = "Pick", description = "픽!")
@RestController
class PickController(
    private val pickUseCase: PickUseCase,
) {
    @GetMapping("/list")
    @Operation(
        summary = "내가 받은 픽 List API",
        description = "내가 받은 픽들을 정렬하여 보여주는 API. default sort : 최신 순",
        responses = [
            ApiResponse(responseCode = "200", description = "내가 받은 픽 리스트")
        ]
    )
    fun getReceivedPickList(
        // todo : add userinfo     
        @RequestParam(required = false, defaultValue = "LATEST") sort: PickSort
    ): DojoApiResponse<ReceivedPickListGetResponse> {
        val receivedPickList: List<PickUseCase.GetReceivedPick> =
            pickUseCase.getReceivedPickList(PickUseCase.GetReceivedPickListCommand(MemberId("1"), sort))

        val pickResponseList = receivedPickList.map {
            PickResponse(
                questionId = it.questionId,
                questionContent = it.questionContent,
                questionEmojiImageUrl = it.questionEmojiImageUrl,
                totalReceivedPickCount = it.totalReceivedPickCount,
                latestPickedAt = it.latestPickedAt
            )
        }
        return DojoApiResponse.success(ReceivedPickListGetResponse(pickResponseList, sort))
    }
}
