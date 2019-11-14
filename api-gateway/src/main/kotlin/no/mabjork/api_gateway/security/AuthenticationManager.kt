package no.mabjork.api_gateway.security


import no.mabjork.api_gateway.utils.JWTUtil
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import javax.security.sasl.AuthenticationException

@Component
class AuthenticationManager(
        val jwtUtil: JWTUtil
) : ReactiveAuthenticationManager {

    override fun authenticate(auth: Authentication): Mono<Authentication> =
            Mono.just(auth)
                    .filter { it.isAuthenticated }
                    .switchIfEmpty(Mono.error(AuthenticationException()))

}