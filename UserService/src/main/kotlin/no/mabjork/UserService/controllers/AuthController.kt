package no.mabjork.UserService.controllers

import no.mabjork.UserService.entities.User
import no.mabjork.UserService.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class AuthController(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder
) {

    data class AuthUser (
            val username: String,
            val password: String
    )

    @RequestMapping("/auth")
    fun authenticateUser(@RequestBody user : AuthUser) : Mono<ResponseEntity<Void>> =
            userRepository
                    .findByName(user.username)
                    .flatMap {
                        if (passwordEncoder.encode(user.password) == it.password) {
                            Mono.just(user)
                        };
                        else {
                            Mono.empty()
                        };
                    }
                    .map { ResponseEntity<Void>(HttpStatus.OK) }
                    .defaultIfEmpty(ResponseEntity<Void>(HttpStatus.BAD_REQUEST))

}