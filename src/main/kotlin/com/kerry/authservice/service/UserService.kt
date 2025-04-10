package com.kerry.authservice.service

import com.kerry.authservice.domain.User
import com.kerry.authservice.domain.UserRepository
import com.kerry.authservice.exception.InvalidPasswordException
import com.kerry.authservice.exception.UserExistsException
import com.kerry.authservice.exception.UserNotFoundException
import com.kerry.authservice.utils.BCryptUtils
import com.kerry.authservice.utils.JWTClaim
import com.kerry.authservice.utils.JWTProperties
import com.kerry.authservice.utils.JWTUtils
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

data class SignUpRequest(
    val email: String,
    val username: String,
    val password: String,
    val profileUrl: String,
)

data class SignInRequest(
    val email: String,
    val password: String,
)

data class SignInResponse(
    val email: String,
    val username: String,
    val profileUrl: String,
    val token: String,
)

data class UserResponse(
    val email: String,
    val username: String,
    val profileUrl: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

@Service
class UserService(
    private val userRepository: UserRepository,
    private val cacheManager: CoroutineCacheManager<User>,
    private val jwtProperties: JWTProperties,
) {
    companion object {
        private val CACHE_TTL = Duration.ofMinutes(1L)
    }

    suspend fun signUp(signUpRequest: SignUpRequest) {
        with(signUpRequest) {
            userRepository.findByEmail(email)?.let { throw UserExistsException(email) }

            val user =
                User(
                    email = email,
                    username = username,
                    password = BCryptUtils.hash(password),
                    profileUrl = profileUrl,
                )

            userRepository.save(user)
        }
    }

    suspend fun signIn(signInRequest: SignInRequest): SignInResponse =
        with(signInRequest) {
            val user =
                userRepository.findByEmail(email)
                    ?: throw UserNotFoundException(email)

            if (!BCryptUtils.verify(password, user.password)) {
                throw InvalidPasswordException(email)
            }

            val token =
                JWTUtils.createToken(
                    claim =
                        JWTClaim(
                            id = user.id!!,
                            name = user.username,
                            email = user.email,
                            profileUrl = user.profileUrl,
                        ),
                    properties = jwtProperties,
                )

            cacheManager.awaitPut(
                key = token,
                value = user,
                expireTime = CACHE_TTL,
            )

            SignInResponse(
                email = user.email,
                username = user.username,
                profileUrl = user.profileUrl,
                token = token,
            )
        }

    suspend fun signOut(token: String) {
        cacheManager.awaitEvict(token)
    }

    suspend fun getByToken(token: String): UserResponse {
        val user =
            cacheManager.awaitGet(token) ?: throw UserNotFoundException("User not found")

        return UserResponse(
            email = user.email,
            username = user.username,
            profileUrl = user.profileUrl,
            createdAt = user.createdAt!!,
            updatedAt = user.updatedAt!!,
        )
    }
}
