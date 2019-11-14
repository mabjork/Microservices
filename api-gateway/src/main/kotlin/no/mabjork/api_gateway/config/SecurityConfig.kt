package no.mabjork.api_gateway.config


import no.mabjork.api_gateway.config.ApplicationConfig.Companion.TOKEN_HEADER
import no.mabjork.api_gateway.config.ApplicationConfig.Companion.TOKEN_PREFIX
import no.mabjork.api_gateway.utils.JWTUtil
import no.mabjork.api_gateway.utils.TokenType
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
        val authManager: ReactiveAuthenticationManager
) {


    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity,
                                  @Qualifier("authFilterDefault")
                                  authFilterDefault: AuthenticationWebFilter
    ): SecurityWebFilterChain {

        http
                .addFilterAt(authFilterDefault, SecurityWebFiltersOrder.AUTHENTICATION)
                .authenticationManager(authManager)
                .authorizeExchange()
                .pathMatchers("/api/**").authenticated()
                .pathMatchers(HttpMethod.POST,"/api/auth").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/user/").permitAll()
                .and()

                .httpBasic()
                .disable()
                .formLogin()
                .disable()
                .csrf()
                .disable()
                .cors()
                .disable()
                .authorizeExchange()
                .anyExchange()
                .denyAll()

        return http.build()
    }

    @Bean("authFilterDefault")
    fun authFilterResourceA(
            authManager: ReactiveAuthenticationManager,
            jwtUtil: JWTUtil
    ): AuthenticationWebFilter {

        val filter = AuthenticationWebFilter(authManager)

        filter.setServerAuthenticationConverter { exchange ->
            Mono.justOrEmpty(exchange)
                    .map { it.request.headers.getFirst(TOKEN_HEADER) ?: "" }
                    .filter { it.startsWith(TOKEN_PREFIX) }
                    .map { it.replace(TOKEN_PREFIX, "") }
                    .filter { jwtUtil.validateToken(it, TokenType.FRONTEND) }
                    .map { token ->
                        val claims = jwtUtil.getAllClaimsFromFrontendToken(token)
                        val username = jwtUtil.getUsernameFromToken(claims)
                        val roles = claims.get("role", List::class.java).map { it as String }
                        UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                roles.map { SimpleGrantedAuthority("ROLE_${it}") }
                        ) as Authentication
                    }
                    .doOnError { Mono.empty<Authentication>() }
        }

        return filter
    }
}
