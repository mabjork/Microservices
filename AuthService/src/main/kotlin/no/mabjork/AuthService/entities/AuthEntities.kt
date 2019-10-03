package no.mabjork.AuthService.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("sessions")
data class Session(
        @Id val id: Int? = null,
        val body: String
)