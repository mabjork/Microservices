package no.mabjork.UserService.config

import no.mabjork.UserService.services.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
 class SecurityConfig(
        val userService: UserService
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
                .authorizeExchange()
                .pathMatchers("/user/*").permitAll()
                .pathMatchers("/user/auth/*").permitAll()
                .anyExchange().authenticated()
                .and().build()
    }
}