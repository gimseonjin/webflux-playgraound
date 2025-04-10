package com.kerry.authservice.utils

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class JWTUtilsTest {
    @Test
    fun `유저 정보로 토큰을 발급할 수 있다`() {
        // given
        val jwtClaim =
            JWTClaim(
                id = 1L,
                name = "kerry",
                email = "kerry@gmail.com",
                profileUrl = "https://example.com/profile.jpg",
            )
        val jwtProperties =
            JWTProperties(
                issuer = "testIssuer",
                subject = "testAudience",
                expiration = 3600L,
                secretKey = "testSecretKey",
            )

        // when
        val token =
            JWTUtils.createToken(
                claim = jwtClaim,
                properties = jwtProperties,
            )

        // then
        assertNotNull(token)
    }

    @Test
    fun `토큰을 decode 하면 유효한 데이터를 볼 수 있다`() {
        // given
        val jwtClaim =
            JWTClaim(
                id = 1L,
                name = "kerry",
                email = "kerry@gmail.com",
                profileUrl = "https://example.com/profile.jpg",
            )
        val jwtProperties =
            JWTProperties(
                issuer = "testIssuer",
                subject = "testAudience",
                expiration = 3600L,
                secretKey = "testSecretKey",
            )
        val token =
            JWTUtils.createToken(
                claim = jwtClaim,
                properties = jwtProperties,
            )

        // when
        val decode =
            JWTUtils.decodeToken(
                token = token,
                properties = jwtProperties,
            )

        // then
        with(decode) {
            assertEquals(claims["id"]?.asLong(), jwtClaim.id)
            assertEquals(claims["name"]?.asString(), jwtClaim.name)
            assertEquals(claims["email"]?.asString(), jwtClaim.email)
            assertEquals(claims["profileUrl"]?.asString(), jwtClaim.profileUrl)
        }
    }
}
