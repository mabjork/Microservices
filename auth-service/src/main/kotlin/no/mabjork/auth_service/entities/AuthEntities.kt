package no.mabjork.auth_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("sessions")
data class Session(
        @Id val id: Int? = null,
        val body: String
)

data class AuthUser(
        val username: String,
        val password: String
)

data class AuthResponse (
        val token: String?
)

