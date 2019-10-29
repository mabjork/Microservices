package no.mabjork.AuthService.services

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class SecretService {

    fun getTokenPublicKey(): Mono<ByteArray> {
        return Mono.just("".toByteArray())
    }

    fun getTokenPrivateKey(): Mono<ByteArray> {
        return Mono.just("".toByteArray())
    }

}