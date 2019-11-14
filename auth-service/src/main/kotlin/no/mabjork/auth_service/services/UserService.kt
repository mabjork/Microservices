package no.mabjork.auth_service.services

import no.mabjork.auth_service.entities.AuthUser
import no.mabjork.auth_service.entities.User
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


interface UserService {
    fun authenticateUser(user: AuthUser): Mono<Boolean>
    fun authenticateUserWithResponse(user: AuthUser): Mono<User>
}

@Service
class UserServiceImpl(
        val webClient: WebClient.Builder
) : UserService {
    override fun authenticateUser(user: AuthUser): Mono<Boolean> =
            webClient.build()
                    .post()
                    .uri("http://user-service/auth/")
                    .body(BodyInserters.fromValue(user))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .map { response -> response.statusCode() == HttpStatus.OK }


    override fun authenticateUserWithResponse(user: AuthUser): Mono<User> =
            webClient.build()
                    .post()
                    .uri("http://user-service/auth/")
                    .body(BodyInserters.fromValue(user))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(User::class.java)
}