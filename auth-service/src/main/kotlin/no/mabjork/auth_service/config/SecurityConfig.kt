package no.mabjork.auth_service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
        val authenticationManager: ReactiveAuthenticationManager
) {
    @Bean
    fun securitygWebFilterChain(http: ServerHttpSecurity) : SecurityWebFilterChain {
        return http
                .exceptionHandling()
                .authenticationEntryPoint { swe, e ->
                    Mono.fromRunnable { swe.response.statusCode = HttpStatus.UNAUTHORIZED }
                }
                .accessDeniedHandler { swe, e ->
                    Mono.fromRunnable { swe.response.statusCode = HttpStatus.FORBIDDEN }
                }
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST,"/auth").permitAll()
                .pathMatchers(HttpMethod.POST,"/auth/token").permitAll()
                .anyExchange().authenticated()
                .and().build()
    }
}