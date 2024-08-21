package com.mashup.dojo.dto

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "마이 스페이스, 친구 스페이스 상세 내역")
data class SpacePickDetail(
    @Schema(description = "픽의 고유 ID", example = "pickId1")
    val pickId: String,
    @Schema(description = "픽의 순위", example = "1")
    val rank: Int,
    @Schema(description = "픽의 내용", example = "대충 작업해도 퀄리티 잘 내오는 사람은?")
    val pickContent: String,
    @Schema(description = "픽의 투표 수", example = "999")
    val pickCount: Int,
    @Schema(description = "픽이 생성된 날짜", example = "2024-08-12T17:18:52.132Z")
    val createdAt: LocalDateTime,
)

@Schema(description = "마이스페이스 - 내가 받은 픽")
data class MySpacePickResponse(
    @ArraySchema(arraySchema = Schema(description = "MySpace 내가 받은 픽 목록"))
    val mySpaceResponses: List<SpacePickDetail>,
)

@Schema(description = "친구 스페이스 - 친구가 받은 픽")
data class FriendSpacePickResponse(
    @ArraySchema(arraySchema = Schema(description = "FriendSpace 친구가 받은 픽 목록"))
    val friendSpaceResponses: List<SpacePickDetail>,
)
