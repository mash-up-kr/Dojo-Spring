package com.mashup.dojo.config.security

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.domain.MemberId
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Jwts.SIG
import io.jsonwebtoken.security.Keys
import java.time.Instant
import java.util.Date

class MemberAuthToken(
    val credentials: String,
) {
    override fun toString(): String {
        return credentials
    }
}

private val logger = KotlinLogging.logger { }

class JwtTokenService(
    private val secretKey: String,
) {
    companion object {
        // 30일
        private val ACCESS_TOKEN_VALID_TIME = 30 * 24 * 60 * 60 * 1000L
        private val MEMBER_ID_CLAIM_KEY = "member_id"
    }

    fun createToken(memberId: MemberId): MemberAuthToken {
        val now: Instant = Instant.now()
        val expiration = Date.from(now.plusSeconds(ACCESS_TOKEN_VALID_TIME))

        val token =
            Jwts.builder()
                .claim(MEMBER_ID_CLAIM_KEY, memberId.value)
                .expiration(expiration)
                .issuedAt(Date.from(now))
                .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()), SIG.HS256)
                .compact()
        return MemberAuthToken(token)
    }

    fun isExpired(token: MemberAuthToken): Boolean {
        val claims = getClaims(token)

        return claims.expiration.before(Date())
    }

    fun getMemberId(token: MemberAuthToken): MemberId? {
        val claims = getClaims(token)

        return if (claims.containsKey(MEMBER_ID_CLAIM_KEY).not()) {
            null
        } else {
            val memberId = claims.get(MEMBER_ID_CLAIM_KEY, String::class.java)
            MemberId(memberId)
        }
    }

    private fun getClaims(token: MemberAuthToken): Claims {
        return kotlin.runCatching {
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
                .build()
                .parseSignedClaims(token.credentials)
                .payload
        }.onFailure { error ->
            logger.info { "Error parsing token: ${error.message}" }
        }.getOrElse {
            throw DojoException.of(DojoExceptionType.INVALID_TOKEN)
        }
    }
}
