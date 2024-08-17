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
            val isPublicPath =
                request.servletPath.startsWith("/public") ||
                    request.servletPath.startsWith("/swagger") ||
                    request.servletPath.startsWith("/v3/api-docs") ||
                    request.servletPath.startsWith("/health")

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
            // 헤더 자체를 trim
            val header = request.getHeader(AUTHORIZATION_HEADER_NAME)?.trim()
            logger.info("Authorization header = $header")

            // "Bearer " 접두사 제거
            val token = header?.takeIf { it.startsWith(BEARER_PREFIX) }?.substring(BEARER_START_INDEX)?.trim()
            logger.info("Token after removing Bearer and trimming = $token")

            // token이 null이 아닌 경우에만 MemberAuthToken 생성
            token?.let { MemberAuthToken(it) }
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
        private const val BEARER_PREFIX = "Bearer "
        private const val BEARER_START_INDEX = 7
    }
}
