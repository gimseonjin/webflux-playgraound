package com.kerry.authservice.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import java.util.*

object JWTUtils {
    fun createToken(
        claim: JWTClaim,
        properties: JWTProperties,
    ) = JWT
        .create()
        .withIssuer(properties.issuer)
        .withAudience(properties.subject)
        .withClaim("id", claim.id)
        .withClaim("name", claim.name)
        .withClaim("email", claim.email)
        .withClaim("profileUrl", claim.profileUrl)
        .withExpiresAt(Date(System.currentTimeMillis() + properties.expiration))
        .sign(Algorithm.HMAC256(properties.secretKey))

    fun decodeToken(
        token: String,
        properties: JWTProperties,
    ): DecodedJWT {
        val verifier =
            JWT
                .require(Algorithm.HMAC256(properties.secretKey))
                .withIssuer(properties.issuer)
                .withAudience(properties.subject)
                .build()

        return verifier.verify(token)
    }
}

data class JWTClaim(
    val id: Long,
    val name: String,
    val email: String,
    val profileUrl: String,
)

@ConfigurationProperties(prefix = "jwt")
data class JWTProperties
    @ConstructorBinding
    constructor(
        val issuer: String,
        val subject: String,
        val expiration: Long,
        val secretKey: String,
    )
