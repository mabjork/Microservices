package no.mabjork.UserService.controllers

import no.mabjork.UserService.entities.AuthResponse
import no.mabjork.UserService.repositories.UserRepository
import no.mabjork.UserService.entities.AuthRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController("/user/auth")
class AuthController(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder
) {


    @PostMapping("/")
    fun authenticateUser(@RequestBody user : AuthRequest) : Mono<ResponseEntity<AuthResponse>> =
            userRepository
                    .findByName(user.username)
                    .flatMap {
                        if (passwordEncoder.encode(user.password) == it.password) {
                            Mono.just(it)
                        }
                        else {
                            Mono.empty()
                        }
                    }
                    .map { ResponseEntity.ok(AuthResponse(it.username, it.roles)) }
                    .defaultIfEmpty(ResponseEntity.badRequest().build())

}