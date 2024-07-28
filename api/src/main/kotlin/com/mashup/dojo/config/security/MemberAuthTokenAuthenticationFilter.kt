package com.mashup.dojo.config.security

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.MemberId
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.filter.OncePerRequestFilter

class MemberAuthTokenAuthenticationFilter(
    private val memberAuthTokenAuthenticationProvider: MemberAuthTokenAuthenticationProvider,
): OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        // Todo Path 분리 밖에서 할 수 있도록 변경하기
        // path가 public 으로 시작하면 패스
        val isPublicPath = request.servletPath.startsWith("/public")
        if(isPublicPath) filterChain.doFilter(request, response)
        else {
            val token = resolveMemberAuthToken(request) ?: throw DojoException.of(DojoExceptionType.AUTHENTICATION_FAILURE, "토큰을 찾을 수 없어요")
            val memberPrincipal = memberAuthTokenAuthenticationProvider.authenticate(token)

            filterChain.doFilter(MemberAuthenticatedHttpServletRequestWrapper(memberPrincipal, ServletWebRequest(request, response)), response)
        }
    }

    private fun resolveMemberAuthToken(request: HttpServletRequest): MemberAuthToken? {
        return kotlin.runCatching {  
            val token = request.getHeader(AUTHORIZATION_HEADER_NAME)
            MemberAuthToken(token)
        }.getOrNull()
    }
    
    companion object {
        private const val AUTHORIZATION_HEADER_NAME = "Authorization";
    }
}


