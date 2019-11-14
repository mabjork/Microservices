package no.mabjork.auth_service.repositories

import no.mabjork.auth_service.entities.Session
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SessionRepository : ReactiveMongoRepository<Session, Int> {

}
