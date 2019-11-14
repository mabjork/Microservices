package no.mabjork.user_service.entities

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "users")
data class User (
        @Id
        val id: String = UUID.randomUUID().toString(),
        val username: String,
        val email: String,
        val firstName: String,
        val lastName: String,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        val password: String,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        val roles: List<Role> = listOf(Role.USER)
)


data class CreateUser(
        val username: String,
        val email: String,
        val firstName: String,
        val lastName: String,
        val password: String
)

data class AuthResponse (
        val username: String,
        val roles: List<Role>
)

data class AuthRequest (
        val username: String,
        val password: String
)

enum class Role (val role : String) {
    ADMIN("ADMIN"), USER("USER")
}