package no.mabjork.ApiGateway.service

import no.mabjork.ApiGateway.models.AuthUser
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


interface AuthService {
    fun authenticate(user: AuthUser): Mono<AuthReponse>
    fun getToken(username: String): Mono<AuthReponse>
}

@Service
class AuthServiceImpl(
        @Qualifier("customWebClient")
        val webClient: WebClient,
        val discoveryClient: EurekaDiscoveryClient
) : AuthService {

    override fun authenticate(user: AuthUser): Mono<AuthReponse> {
        val serviceName = "auth-service"
        val endpoint = "auth"
        val service = discoveryClient.getInstances(serviceName).random()
        return webClient
                .post()
                .uri("http://${service.host}:${service.port}/$endpoint")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .retrieve()
                .bodyToMono(AuthReponse::class.java)
    }

    override fun getToken(username: String): Mono<AuthReponse> {
        return Mono.just(AuthReponse("Token"))
    }
}

data class AuthReponse (
        val token: String?
)