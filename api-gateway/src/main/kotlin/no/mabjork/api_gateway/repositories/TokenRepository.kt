package no.mabjork.api_gateway.repositories

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


interface TokenRepository {
    fun getToken(key: String): Mono<Token>
    fun storeToken(key: String, token: String) : Mono<Void>
}

@Document(collection = "tokens")
data class Token(
        @Id
        val frontendToken: String,
        val backendToken: String
)


@Component("inMemoryTokenRepository")
class InMemoryTokenRepository : TokenRepository {

    companion object {
        val tokens = hashMapOf<String, Token>()
    }

    override fun getToken(key: String): Mono<Token> {
        return Mono.justOrEmpty(tokens[key])
    }

    override fun storeToken(key: String, token: String): Mono<Void> {
        tokens[key] = Token(key,token)
        return Mono.empty<Void>()
    }

}

@Component("mongoTokenRepository")
class MongoTokenRepositoryImpl(
        val mongoTemplate: ReactiveMongoTemplate
) : TokenRepository {

    override fun getToken(key: String): Mono<Token> {
        return mongoTemplate.findById(key, Token::class.java)
    }

    override fun storeToken(key: String, token: String): Mono<Void> {
        mongoTemplate.insert(Token(key,token)).subscribe()
        return Mono.empty()
    }
}