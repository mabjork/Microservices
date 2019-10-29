package no.mabjork.AuthService.entities

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import java.util.*

data class User (
        @Id
        val id: String = UUID.randomUUID().toString(),
        val username: String,
        val email: String = "",
        val firstName: String = "",
        val lastName: String = "",
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        val password: String,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        val roles: List<Role> = listOf(Role.USER, Role.ADMIN)
)

enum class Role (val role : String) {
    ADMIN("ADMIN"), USER("USER")
}