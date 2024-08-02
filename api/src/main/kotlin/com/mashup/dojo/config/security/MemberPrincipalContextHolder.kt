package com.mashup.dojo.config.security

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType.AUTHENTICATION_FAILURE
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

object MemberPrincipalContextHolder {
    fun current(): MemberPrincipal {
        return when (val attributes = RequestContextHolder.currentRequestAttributes()) {
            is ServletRequestAttributes -> {
                val principal = attributes.request.userPrincipal ?: throw DojoException.of(AUTHENTICATION_FAILURE, "principal not found")
                if (principal is MemberPrincipal) {
                    principal
                } else {
                    throw DojoException.of(AUTHENTICATION_FAILURE, "invalid principal")
                }
            }
            else -> throw DojoException.of(AUTHENTICATION_FAILURE, "principal not found")
        }
    }
}
