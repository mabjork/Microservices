package no.mabjork.api_gateway.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import no.mabjork.api_gateway.models.AuthUser
import no.mabjork.api_gateway.service.SecretService
import org.springframework.stereotype.Component
import java.security.PrivateKey
import java.util.*
import kotlin.collections.HashMap


enum class TokenType {
    FRONTEND, BACKEND
}

@Component
class JWTUtil(
        private val secretService: SecretService
) {

    fun getAllClaimsFromFrontendToken(token: String): Claims =
            Jwts
                    .parser()
                    .setSigningKey(secretService.getTokenPublicKey())
                    .parseClaimsJws(token).body


    fun getAllClaimsFromBackendToken(token: String): Claims =
            Jwts
                    .parser()
                    .setSigningKey(secretService.getAuthServicePublicKey())
                    .parseClaimsJws(token).body

    fun getUsernameFromToken(claims: Claims): String = claims.subject

    fun getExpirationDateFromToken(claims: Claims): Date = claims.expiration

    fun generateToken(user: AuthUser, roles: List<String>): String {
        val claims = HashMap<String, Any>()
        claims["role"] = roles
        return doGenerateToken(claims, user.username, secretService.getTokenPrivateKey())
    }

    fun validateToken(token: String, tokenType: TokenType): Boolean =
            if( tokenType == TokenType.BACKEND ){
                val claims = getAllClaimsFromBackendToken(token)
                !isTokenExpired(claims)
            }else {
                val claims = getAllClaimsFromFrontendToken(token)
                !isTokenExpired(claims)
            }


    private fun doGenerateToken(claims: Map<String, Any>, username: String, key: PrivateKey): String =
            Jwts
                    .builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(Date())
                    .setExpiration(Date(Date().time + secretService.getTokenExpiration() * 1000))
                    .signWith(SignatureAlgorithm.RS512, key)
                    .compact()


    private fun isTokenExpired(claims: Claims): Boolean = getExpirationDateFromToken(claims).before(Date())
}