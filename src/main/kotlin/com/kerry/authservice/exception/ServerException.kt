package com.kerry.authservice.exception

sealed class ServerException(
    val code: Int,
    override val message: String,
) : RuntimeException(message)

data class UserExistsException(
    val email: String,
) : ServerException(
        code = 400,
        message = "User with email $email already exists",
    )

data class UserNotFoundException(
    val email: String,
) : ServerException(
        code = 404,
        message = "User with email $email not found",
    )

data class InvalidPasswordException(
    val email: String,
) : ServerException(
        code = 401,
        message = "Invalid password for user with email $email",
    )
