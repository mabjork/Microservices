package no.mabjork.UserService.controllers

import no.mabjork.UserService.entities.User
import no.mabjork.UserService.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid


@RestController
class UserController(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder) {

    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: String): Mono<ResponseEntity<User>> =
            userRepository
                    .findById(id)
                    .map { user -> ResponseEntity.ok(user) }
                    .defaultIfEmpty(ResponseEntity.badRequest().build())


    @PostMapping("/users")
    fun createUser(@Valid @RequestBody user: User): Mono<ResponseEntity<User>> =
            userRepository.findByName(user.username)
                    .map { ResponseEntity<User>(HttpStatus.BAD_REQUEST) }
                    .switchIfEmpty(
                            userRepository
                                    .save(User(
                                            username = user.username,
                                            password = passwordEncoder.encode(user.password),
                                            lastName = user.lastName,
                                            firstName = user.firstName,
                                            email = user.email
                                            )
                                    )
                                    .map { ResponseEntity.ok(it) }
                    )

    @PutMapping("/users/{id}")
    fun updateUser(@PathVariable id: String, @Valid @RequestBody user: User): Mono<ResponseEntity<User>> =
            userRepository
                    .findById(id).flatMap { existingUser ->
                        val updatedUser = User(
                                id = id,
                                email = user.email,
                                firstName = user.firstName,
                                lastName = user.lastName,
                                roles = existingUser.roles,
                                username = existingUser.username,
                                password = existingUser.password
                        )
                        userRepository.save(updatedUser)
                    }
                    .map { updatedUser -> ResponseEntity.ok(updatedUser) }
                    .defaultIfEmpty(ResponseEntity.badRequest().build())


    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: String) : Mono<ResponseEntity<Void>> =
            userRepository
                    .findById(id)
                    .flatMap { existingUser -> userRepository.delete(existingUser) }
                    .map { ResponseEntity<Void>(HttpStatus.OK) }
                    .defaultIfEmpty( ResponseEntity<Void>(HttpStatus.NOT_FOUND) )


}