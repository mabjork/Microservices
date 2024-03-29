package no.mabjork.auth_service.security


import no.mabjork.auth_service.entities.Role
import no.mabjork.auth_service.utils.JWTUtil
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthenticationManager(
        val jwtUtil: JWTUtil
) : ReactiveAuthenticationManager {

    override fun authenticate(auth: Authentication): Mono<Authentication> =
            Mono.just(auth.credentials.toString())
                    .flatMap { authToken ->
                        jwtUtil.getUsernameFromToken(authToken)
                                .flatMap { username ->
                                    jwtUtil.validateToken(authToken)
                                            .filter { valid -> valid }
                                            .flatMap { jwtUtil.getAllClaimsFromToken(authToken) }
                                            .map { claims ->
                                                claims.get("roles", List::class.java)
                                                        .map { role -> Role.valueOf(role as String) }
                                            }
                                            .map { roles ->
                                                UsernamePasswordAuthenticationToken(
                                                        username,
                                                        null,
                                                        roles.map { SimpleGrantedAuthority("ROLE_$it") })
                                            }.map { it as Authentication }
                                }.defaultIfEmpty(UsernamePasswordAuthenticationToken(null,null,null) as Authentication)
                    }.defaultIfEmpty(UsernamePasswordAuthenticationToken(null,null,null) as Authentication)
}