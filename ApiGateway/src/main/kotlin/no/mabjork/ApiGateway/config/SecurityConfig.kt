package no.mabjork.ApiGateway.config


import com.fasterxml.jackson.databind.ObjectMapper
import no.mabjork.ApiGateway.models.AuthUser
import no.mabjork.ApiGateway.service.AuthService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.io.InputStream
import java.io.SequenceInputStream


@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
        val mapper: ObjectMapper,
        val authService: AuthService
) {

    @Bean
    fun getReactiveAuthenticationManager(): ReactiveAuthenticationManager {
        return ReactiveAuthenticationManager { authentication ->
            // simply return the authentication assuming the authentication was already verified in the converter
            Mono.justOrEmpty(authentication)
        }
    }

    companion object {
        const val ADMIN_RESOURCE_A = "ADMIN_RESOURCE_A"
        const val ADMIN_RESOURCE_B = "ADMIN_RESOURCE_B"
    }

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity,
                                  @Qualifier("authFilterLogon")
                                  authFilterLogon: AuthenticationWebFilter,
                                  @Qualifier("authFilterDefault")
                                  authFilterDefault: AuthenticationWebFilter): SecurityWebFilterChain {

        //configure security for resource b
        http
                .addFilterAt(authFilterLogon, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange()
                .pathMatchers("/api/**")
                .hasRole(ADMIN_RESOURCE_B)

        // global config
        http
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
    fun authFilterResourceA(authManager: ReactiveAuthenticationManager): AuthenticationWebFilter {

        val filter = AuthenticationWebFilter(authManager)

        filter.setServerAuthenticationConverter {

            // simplified dummy token conversion for keeping the example as simple as possible
            Mono.justOrEmpty(it)
                    .map { it.request.headers.getFirst("Authentication") ?: "" }
                    .filter { it == "Bearer Token" }
                    .map {
                        val authentication = UsernamePasswordAuthenticationToken(
                                "userWithAccessRightsToA",
                                it,
                                listOf(SimpleGrantedAuthority("ROLE_$ADMIN_RESOURCE_A"))
                        )


                        authentication as Authentication
                    }
        }

        return filter
    }

    @Bean("authFilterLogon")
    fun authFilterResourceB(authManager: ReactiveAuthenticationManager): AuthenticationWebFilter {

        val filter = AuthenticationWebFilter(authManager)

        filter.setServerAuthenticationConverter { exchange ->

            Mono.justOrEmpty(exchange).map { it.request.headers }.map { it["Authentication"] }


            exchange.request.body.reduce(object : InputStream() {
                override fun read() = -1
            }) { s: InputStream, d -> SequenceInputStream(s, d.asInputStream()) }
                    .map { inputStream -> mapper.readValue(inputStream, AuthUser::class.java) }
                    .flatMap { user -> authService.authenticate(user) }
                    .filter { response -> response.token != null }
                    .map{ response ->

                        val request = exchange.request
                                .mutate()
                                .header("Authentication", response.token)
                                .build()

                        exchange.mutate().request(request)

                        val authentication: Authentication = UsernamePasswordAuthenticationToken(
                                "",
                                "",
                                listOf(SimpleGrantedAuthority("ROLE_$ADMIN_RESOURCE_A")))

                        authentication
                    }
        }
        return filter
    }
}
