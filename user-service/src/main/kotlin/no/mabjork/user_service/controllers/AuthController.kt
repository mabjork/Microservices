package no.mabjork.user_service.controllers

import no.mabjork.user_service.entities.AuthResponse
import no.mabjork.user_service.repositories.UserRepository
import no.mabjork.user_service.entities.AuthRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController()
class AuthController(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder
) {


    @PostMapping("/auth/")
    fun authenticateUser(@RequestBody user : AuthRequest) : Mono<ResponseEntity<AuthResponse>> =
            userRepository
                    .findByName(user.username)
                    .flatMap {
                        if (passwordEncoder.matches(user.password, it.password)) {
                            Mono.just(it)
                        }
                        else {
                            Mono.empty()
                        }
                    }
                    .map { ResponseEntity.ok(AuthResponse(it.username, it.roles)) }
                    .defaultIfEmpty(ResponseEntity.badRequest().build())

}