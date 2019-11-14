package no.mabjork.api_gateway.service

import no.mabjork.api_gateway.repositories.TokenRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


interface TokenService {
    fun exchangeToken(token: String): Mono<String>
    fun saveBackendToken(frontendToken: String, backendToken: String)
}

@Service
class TokenServiceImpl(
        @Qualifier("inMemoryTokenRepository")
        private val tokenRepository: TokenRepository,
        @Qualifier("mongoTokenRepository")
        private val mongoTokenRepository: TokenRepository,
        private val secretService: SecretService
) : TokenService {

    override fun exchangeToken(token: String): Mono<String> {
        return tokenRepository
                .getToken(token)
                .map { storedToken -> storedToken.backendToken }
                .switchIfEmpty(
                        mongoTokenRepository.getToken(secretService.hashToken(token))
                                .map { storedToken -> secretService.decryptToken(storedToken.backendToken) }
                )
    }

    override fun saveBackendToken(frontendToken: String, backendToken: String) {
        val hashedToken = secretService.hashToken(frontendToken);
        val encryptedToken = secretService.encryptToken(backendToken)
        tokenRepository.storeToken(frontendToken, backendToken)
        mongoTokenRepository.storeToken(hashedToken, encryptedToken)
    }

}