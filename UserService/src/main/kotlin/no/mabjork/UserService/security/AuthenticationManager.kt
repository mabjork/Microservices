package no.mabjork.UserService.security

import no.mabjork.UserService.entities.Role
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthenticationManager(
        val jwtUtil: JWTUtil
) : ReactiveAuthenticationManager {

    override fun authenticate(auth: Authentication?): Mono<Authentication> {
        val authToken = auth?.credentials.toString()

        var username : String?

        try {
            username = jwtUtil.getUsernameFromToken(authToken)
        } catch (e : Exception){
            username = null
        }

        if (username !== null && jwtUtil.validateToken(authToken)){
            val claims = jwtUtil.getAllClaimsFromToken(authToken)
            val rolesMap = claims.get("roles", List::class.java)
            val roles = ArrayList<Role>()
            for (role in rolesMap) {
                roles.add(Role.valueOf(role as String))
            }

            val authentication = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    null
            )

            return Mono.just(authentication)

        }
        else{
            return Mono.empty()
        }
    }

}