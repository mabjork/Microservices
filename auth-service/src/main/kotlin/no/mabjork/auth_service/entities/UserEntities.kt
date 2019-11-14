package no.mabjork.auth_service.entities


data class User (
        val username: String,
        val roles: List<Role>
)

enum class Role (val role : String) {
    ADMIN("ADMIN"), USER("USER")
}