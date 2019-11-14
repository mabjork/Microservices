package no.mabjork.auth_service.controllers

import no.mabjork.auth_service.entities.AuthResponse
import no.mabjork.auth_service.entities.AuthUser
import no.mabjork.auth_service.utils.JWTUtil
import no.mabjork.auth_service.services.UserService
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
                    .flatMap { user -> userService.authenticateUserWithResponse(user) }
                    .map { authenticatedUser -> jwtUtil.generateToken(authenticatedUser) }
                    .flatMap { token -> token.map { AuthResponse(it) } }

    @PostMapping("/auth/token")
    fun authToken(@RequestBody token: AuthResponse): Mono<Void> =
            Mono.justOrEmpty(token)
                    .flatMap { token ->
                        jwtUtil.getAllClaimsFromToken(token.token ?: "")
                                .map {claims ->
                                    if(claims.isEmpty()){
                                        null
                                    }else {
                                        null
                                    }
                                }
                    }

}

