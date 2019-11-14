package no.mabjork.user_service.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.config.EnableWebFlux


@Configuration
@EnableWebFlux
class WebFluxConfig : WebFluxConfigurer