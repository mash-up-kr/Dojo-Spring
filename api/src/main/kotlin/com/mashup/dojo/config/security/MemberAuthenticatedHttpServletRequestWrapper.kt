package com.mashup.dojo.config.security

import jakarta.servlet.http.HttpServletRequestWrapper
import org.springframework.web.context.request.ServletWebRequest
import java.security.Principal

class MemberAuthenticatedHttpServletRequestWrapper(
    private val principal: MemberPrincipal,
    private val request: ServletWebRequest
): HttpServletRequestWrapper(request.request) {
    override fun getUserPrincipal(): Principal = principal
}
