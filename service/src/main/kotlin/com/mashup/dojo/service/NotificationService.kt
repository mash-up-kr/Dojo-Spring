package com.mashup.dojo.service

import com.mashup.dojo.domain.Member
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.PickId
import com.mashup.dojo.domain.QuestionId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

interface NotificationService {
    fun notifyPicked(
        target: Member,
        questionId: QuestionId,
        pickId: PickId,
    )
}

/**
 * SSE (Server Sent Event) 방식으로 발송되는 알림
 */
@Service
class SSENotificationService : NotificationService {
    private val emitters = ConcurrentHashMap<String, SseEmitter>()
    private val logger = KotlinLogging.logger { }

    override fun notifyPicked(
        target: Member,
        questionId: QuestionId,
        pickId: PickId,
    ) {
        data class NotifyPickedEvent(val questionId: QuestionId, val pickId: PickId, val ordinal: Int)

        val emitter = emitters[target.id.value]
        emitter?.send(
            SseEmitter.event()
                .name("picked")
                .data(
                    NotifyPickedEvent(
                        questionId = questionId,
                        pickId = pickId,
                        ordinal = target.ordinal
                    )
                )
        ) ?: run {
            logger.warn { "emitter not found, memberId: ${target.id.value}" }
        }
    }

    fun addEmitter(
        memberId: MemberId,
        emitter: SseEmitter,
    ) {
        emitters[memberId.value] = emitter
        emitter.onCompletion {
            logger.debug { "emitter($memberId) completed successfully" }
            emitters.remove(memberId.value)
        }
        emitter.onError {
            logger.debug { "emitter error occurred: $it" }
            emitters.remove(memberId.value)
        }
        emitter.onTimeout {
            logger.debug { "emitter($memberId) timed out" }
            emitters.remove(memberId.value)
        }
    }
}
