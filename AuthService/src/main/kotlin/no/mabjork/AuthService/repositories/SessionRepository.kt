package no.mabjork.AuthService.repositories

import no.mabjork.AuthService.entities.Session
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

import reactor.core.publisher.Flux

@Repository
interface SessionRepository : ReactiveMongoRepository<Session, Int> {

}
