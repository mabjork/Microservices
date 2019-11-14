package no.mabjork.api_gateway.config


import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.context.annotation.Bean


@Configuration
class WebFluxConfig {

    @Bean("customWebClient")
    @LoadBalanced
    fun client(builder : WebClient.Builder): WebClient {
        return builder.build()
    }
}
