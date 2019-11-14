package no.mabjork.auth_service.config

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer




@Configuration
@EnableWebFlux
class WebFluxConfig : WebFluxConfigurer {

    @Bean
    @LoadBalanced
    fun loadBalancedWebClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }

    @Bean
    fun propertyConfigInDev(): PropertySourcesPlaceholderConfigurer {
        return PropertySourcesPlaceholderConfigurer()
    }

}