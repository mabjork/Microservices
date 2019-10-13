package no.mabjork.UserService.controllers

import no.mabjork.UserService.entities.AuthResponse
import no.mabjork.UserService.entities.AuthUser
import no.mabjork.UserService.entities.User
import no.mabjork.UserService.repositories.UserRepository
import no.mabjork.UserService.security.JWTUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
class AuthController(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
        private val jwtUtil: JWTUtil
) {


    @PostMapping("/login")
    fun authenticateUser(@RequestBody user : AuthUser) : Mono<ResponseEntity<AuthResponse>> =
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
                    .map { ResponseEntity.ok(AuthResponse(jwtUtil.generateToken(it))) }
                    .defaultIfEmpty(ResponseEntity.badRequest().build())

}