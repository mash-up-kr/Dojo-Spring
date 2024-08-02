package com.mashup.dojo.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType.AUTHENTICATION_FAILURE
import com.mashup.dojo.common.DojoApiErrorResponse
import com.mashup.dojo.common.DojoApiResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.filter.OncePerRequestFilter

class MemberAuthTokenAuthenticationFilter(
    private val memberAuthTokenAuthenticationProvider: MemberAuthTokenAuthenticationProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            // Todo Path 분리 밖에서 할 수 있도록 변경하기
            // path가 public 으로 시작하면 패스
            val isPublicPath = request.servletPath.startsWith("/public") || request.servletPath.startsWith("/swagger") || request.servletPath.startsWith("/v3/api-docs")
            if (isPublicPath) {
                filterChain.doFilter(request, response)
            } else {
                val token = resolveMemberAuthToken(request) ?: throw DojoException.of(AUTHENTICATION_FAILURE, "cannot find token")
                val memberPrincipal = memberAuthTokenAuthenticationProvider.authenticate(token)

                filterChain.doFilter(MemberAuthenticatedHttpServletRequestWrapper(memberPrincipal, ServletWebRequest(request, response)), response)
            }
        } catch (error: DojoException) {
            handleAuthenticateException(response, error)
        }
    }

    private fun resolveMemberAuthToken(request: HttpServletRequest): MemberAuthToken? {
        return kotlin.runCatching {
            val token = request.getHeader(AUTHORIZATION_HEADER_NAME)
            MemberAuthToken(token)
        }.getOrNull()
    }

    private fun handleAuthenticateException(
        response: HttpServletResponse,
        error: DojoException,
    ) {
        response.status = error.httpStatusCode
        response.contentType = "application/json"

        val dojoResponse =
            DojoApiResponse(
                success = false,
                data = null,
                error =
                    DojoApiErrorResponse(
                        code = error.errorCode,
                        message = error.message
                    )
            )
        response.writer.write(ObjectMapper().writeValueAsString(dojoResponse))
    }

    companion object {
        private const val AUTHORIZATION_HEADER_NAME = "Authorization"
    }
}
