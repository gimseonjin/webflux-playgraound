package com.kerry.authservice.controller

import com.kerry.authservice.config.AuthToken
import com.kerry.authservice.service.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/sign-up")
    suspend fun signUp(
        @RequestBody signUpRequest: SignUpRequest,
    ): Unit = userService.signUp(signUpRequest)

    @PostMapping("/sign-in")
    suspend fun signIn(
        @RequestBody signInRequest: SignInRequest,
    ): SignInResponse = userService.signIn(signInRequest)

    @DeleteMapping("/sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun signOut(
        @AuthToken token: String,
    ): Unit = userService.signOut(token)

    @GetMapping("/me")
    suspend fun getMe(
        @AuthToken token: String,
    ): UserResponse = userService.getByToken(token)
}
