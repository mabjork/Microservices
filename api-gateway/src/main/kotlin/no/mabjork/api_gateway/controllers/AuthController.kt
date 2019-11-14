package no.mabjork.api_gateway.controllers

import no.mabjork.api_gateway.config.ApplicationConfig.Companion.TOKEN_HEADER
import no.mabjork.api_gateway.config.ApplicationConfig.Companion.TOKEN_PREFIX
import no.mabjork.api_gateway.models.AuthUser
import no.mabjork.api_gateway.service.AuthService
import no.mabjork.api_gateway.service.TokenService
import no.mabjork.api_gateway.utils.JWTUtil
import no.mabjork.api_gateway.utils.TokenType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class AuthController(
        private val jwtUtil: JWTUtil,
        private val authService: AuthService,
        private val tokenService: TokenService
) {

    @RequestMapping("/auth")
    fun authenticate(@RequestBody user: AuthUser): Mono<ResponseEntity<Void>> {
        return authService.authenticate(user)
                .map { response ->
                    if (!response.token.isNullOrBlank() && jwtUtil.validateToken(response.token, TokenType.BACKEND)) {
                        val claims = jwtUtil.getAllClaimsFromBackendToken(response.token)
                        val roles = claims["role", List::class.java].map { role -> role as String }
                        val frontendToken = jwtUtil.generateToken(user, roles)
                        tokenService.saveBackendToken(frontendToken, response.token)
                        ResponseEntity.ok().header(TOKEN_HEADER, "$TOKEN_PREFIX$frontendToken").build<Void>()
                    } else {
                        ResponseEntity.badRequest().build()
                    } }
    }
}