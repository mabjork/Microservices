package no.mabjork.auth_service.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import no.mabjork.auth_service.entities.User
import no.mabjork.auth_service.services.SecretService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.security.PrivateKey
import java.util.*
import kotlin.collections.HashMap


@Component
class JWTUtil(
        private val secretService: SecretService
) {

    fun getAllClaimsFromToken(token: String): Mono<Claims> =
            Mono.just(Jwts.parser())
                    .map { parser -> parser.setSigningKey(secretService.getTokenPublicKey()) }
                    .map { parser -> parser.parseClaimsJws(token) }
                    .map { jwt -> jwt.body }

    fun getUsernameFromToken(token: String): Mono<String> = getAllClaimsFromToken(token).map { it.subject }

    fun getExpirationDateFromToken(token: String): Mono<Date> = getAllClaimsFromToken(token).map { it.expiration }

    fun generateToken(user: User): Mono<String> {
        val claims = HashMap<String, Any>()
        claims["role"] = user.roles
        return doGenerateToken(claims, user.username, secretService.getTokenPrivateKey())
    }

    fun validateToken(token: String): Mono<Boolean> = isTokenExpired(token).map { !it }

    private fun doGenerateToken(claims: Map<String, Any>, username: String, key: PrivateKey): Mono<String> =
        Mono.just(Jwts.builder())
                .map { builder -> builder.setClaims(claims) }
                .map { builder -> builder.setSubject(username) }
                .map { builder -> builder.setIssuedAt(Date()) }
                .map { builder -> builder.setExpiration( Date(Date().time + secretService.getTokenExpiration() * 1000)) }
                .map { builder -> builder.signWith(SignatureAlgorithm.RS512, key) }
                .map { builder -> builder.compact() }

    private fun isTokenExpired(token: String): Mono<Boolean> = getExpirationDateFromToken(token).map { date -> date.before(Date()) }
}