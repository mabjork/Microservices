package no.mabjork.ApiGateway.models

data class AuthUser (
        val username: String,
        val password: String
)

enum class Role (val role : String) {
    ADMIN("ADMIN"), USER("USER")
}