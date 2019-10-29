package no.mabjork.AuthService.controllers

import no.mabjork.AuthService.entities.AuthResponse
import no.mabjork.AuthService.entities.AuthUser
import no.mabjork.AuthService.utils.JWTUtil
import no.mabjork.AuthService.services.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class AuthController(
        val jwtUtil: JWTUtil,
        val userService: UserService
) {

    @PostMapping("/auth")
    fun authUser(@RequestBody user: AuthUser): Mono<AuthResponse> =
            Mono.justOrEmpty(user)
                    .flatMap { user -> userService.authenticateUserWithRespone(user) }
                    .map { authenticatedUser -> jwtUtil.generateToken(authenticatedUser) }
                    .flatMap { token -> token.map { AuthResponse(it) } }

}

