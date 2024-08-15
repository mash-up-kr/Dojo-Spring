package com.mashup.dojo

import com.mashup.dojo.config.security.MemberPrincipalContextHolder
import com.mashup.dojo.domain.PickId
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.service.MemberService
import com.mashup.dojo.service.SSENotificationService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
class NotificationSseController(
    private val sseNotificationService: SSENotificationService,
    private val memberService: MemberService,
) {
    private val logger = KotlinLogging.logger { }

    @GetMapping("/notification-stream")
    @Operation(
        summary = "알림을 받기 위해 SSE 커넥션을 연결합니다, timeout: 3분"
    )
    fun stream(): SseEmitter {
        logger.info { "connect sse" }
        // timeout(3분) 동안 아무런 데이터 전송되지 않으면 연결 자동 종료
        val emitter = SseEmitter(180000L)
        val memberId = MemberPrincipalContextHolder.current().id
        sseNotificationService.addEmitter(memberId = memberId, emitter = emitter)
        return emitter
    }

    @PostMapping("/notification-test")
    @Operation(
        summary = "알림 테스트를 위해 사용합니다.",
        description = "해당 API 요청자에게 event가 발송됩니다."
    )
    fun test(
        questionId: String,
        pickId: String,
    ) {
        val memberId = MemberPrincipalContextHolder.current().id
        val member = memberService.findMemberById(memberId) ?: return
        sseNotificationService.notifyPicked(
            target = member,
            questionId = QuestionId(questionId),
            pickId = PickId(pickId)
        )
    }
}
