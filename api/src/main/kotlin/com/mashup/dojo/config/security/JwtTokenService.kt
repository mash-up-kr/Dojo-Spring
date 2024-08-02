package com.mashup.dojo.config.security

import com.mashup.dojo.domain.MemberId
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
        val payload =
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
                .build()
                .parseSignedClaims(token.credentials)
                .payload

        return payload.expiration.before(Date())
    }

    fun getMemberId(token: MemberAuthToken): MemberId? {
        val payload =
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
                .build()
                .parseSignedClaims(token.credentials)
                .payload

        return if (payload.containsKey(MEMBER_ID_CLAIM_KEY).not()) {
            null
        } else {
            val memberId = payload.get(MEMBER_ID_CLAIM_KEY, String::class.java)
            MemberId(memberId)
        }
    }
}
