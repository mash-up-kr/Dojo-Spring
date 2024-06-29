package com.mashup.dojo.sheet

import com.mashup.dojo.sheet.dto.response.MemberResponse
import com.mashup.dojo.sheet.dto.response.SheetResponse
import com.mashup.dojo.usecase.SheetUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/pick")
class SheetController(
    private val sheetUseCase: SheetUseCase
) {

    @GetMapping
    fun getCurrentSheet(): SheetResponse {
        /*
        ToDo
        현재 사용자의 정보를 가져와 질문이 남아있는지, 남아있다면 몇번째의 어떤걸 줘야하는지 판단해서 현재 번호와 질문을 내려줄 것
        현재 사용자와 시간으로 현재 서버 질문 리스트 중에 남아있는 질문이 있는지 계산 
         */
        val pickResponse = sheetUseCase.getCurrentQuestion()
        val memberResponse = pickResponse.members.stream().map { member ->
            MemberResponse(memberId = member.memberId.value, memberName = member.memberName, order = member.order)
        }.toList()

        return SheetResponse(
            questionId = pickResponse.questionId,
            questionSetId = pickResponse.questionSetId,
            imageUrl = pickResponse.imageUrl,
            candidates = memberResponse
        )
    }
}
