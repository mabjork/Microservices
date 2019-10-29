package no.mabjork.ApiGateway.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import no.mabjork.ApiGateway.models.AuthUser
import no.mabjork.ApiGateway.service.SecretService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap


@Component
@PropertySource("classpath:jwt.yml")
class JWTUtil(
        @Value("\${jwt.expiration}")
        private val expirationTime: String,
        private val secretService: SecretService
) {

    fun getAllClaimsFromToken(token: String): Mono<Claims> =
        secretService.getTokenPublicKey()
                .map { key -> Jwts.parser().setSigningKey(key) }
                .map { parser -> parser.parseClaimsJws(token) }
                .map { jwtToken -> jwtToken.body }

    fun getUsernameFromToken(token: String): Mono<String> = getAllClaimsFromToken(token).map { it.subject }

    fun getExpirationDateFromToken(token: String): Mono<Date> = getAllClaimsFromToken(token).map { it.expiration }

    fun generateToken(user: AuthUser): Mono<String> {
        val claims = HashMap<String, Any>()
        //claims["role"] = user.roles
        return secretService.getTokenPrivateKey().flatMap { doGenerateToken(claims, user.username, it) }
    }

    fun validateToken(token: String): Mono<Boolean> = isTokenExpired(token).map { !it }

    private fun doGenerateToken(claims: Map<String, Any>, username: String, key: ByteArray): Mono<String> =
        Mono.just(Jwts.builder())
                .map { builder -> builder.setClaims(claims) }
                .map { builder -> builder.setSubject(username) }
                .map { builder -> builder.setIssuedAt(Date()) }
                .map { builder -> builder.setExpiration( Date(Date().time + expirationTime.toLong() * 1000)) }
                .map { builder -> builder.signWith(SecretKeySpec(key, SignatureAlgorithm.RS512.jcaName)) }
                .map { builder -> builder.compact() }

    private fun isTokenExpired(token: String): Mono<Boolean> = getExpirationDateFromToken(token).map { date -> date.before(Date()) }
}