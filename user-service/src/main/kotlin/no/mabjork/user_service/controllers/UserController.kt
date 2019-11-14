package no.mabjork.user_service.controllers

import no.mabjork.user_service.entities.CreateUser
import no.mabjork.user_service.entities.User
import no.mabjork.user_service.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid


@RestController
class UserController(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder
) {


    @PostMapping("/user/")
    fun createUser(@RequestBody user: CreateUser): Mono<ResponseEntity<User>> =
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

    @GetMapping("/user/{id}")
    fun getUser(@PathVariable id: String): Mono<ResponseEntity<User>> =
            userRepository
                    .findById(id)
                    .map { user -> ResponseEntity.ok(user) }
                    .defaultIfEmpty(ResponseEntity.badRequest().build())



    @PutMapping("/user/{id}")
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


    @DeleteMapping("/user/{id}")
    fun deleteUser(@PathVariable id: String) : Mono<ResponseEntity<Void>> =
            userRepository
                    .findById(id)
                    .flatMap { existingUser -> userRepository.delete(existingUser) }
                    .map { ResponseEntity<Void>(HttpStatus.OK) }
                    .defaultIfEmpty( ResponseEntity<Void>(HttpStatus.NOT_FOUND) )


}