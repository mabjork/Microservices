package no.mabjork.AuthService.services

import no.mabjork.AuthService.entities.AuthUser
import no.mabjork.AuthService.entities.User
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


interface UserService {
    fun authenticateUser(user: AuthUser) : Mono<Boolean>
    fun authenticateUserWithRespone(user: AuthUser) : Mono<User>
}

@Service
class UserServiceImpl(
        val webClient: WebClient
) : UserService {
    override fun authenticateUser(user: AuthUser): Mono<Boolean> =
        webClient
                .post()
                .uri("http://user-service/user/auth/")
                .body(BodyInserters.fromValue(user))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .map { response -> response.statusCode() == HttpStatus.OK }


    override fun authenticateUserWithRespone(user: AuthUser): Mono<User> =
            webClient
                    .post()
                    .uri("http://user-service/user/auth/")
                    .body(BodyInserters.fromValue(user))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(User::class.java)
}