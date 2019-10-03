package no.mabjork.UserService.repositories

import no.mabjork.UserService.entities.User
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveMongoRepository<User, String>  {
    @Query("{ 'username': ?0 }")
    fun findByName(name: String): Mono<User>
}