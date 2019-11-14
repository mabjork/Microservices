package no.mabjork.api_gateway.config

import no.mabjork.api_gateway.config.ApplicationConfig.Companion.FOREX_SERVICE
import no.mabjork.api_gateway.config.ApplicationConfig.Companion.PREDICTION_SERVICE
import no.mabjork.api_gateway.config.ApplicationConfig.Companion.STOCK_SERVICE
import no.mabjork.api_gateway.config.ApplicationConfig.Companion.TOKEN_HEADER
import no.mabjork.api_gateway.config.ApplicationConfig.Companion.TOKEN_PREFIX
import no.mabjork.api_gateway.config.ApplicationConfig.Companion.USER_SERVICE
import no.mabjork.api_gateway.service.TokenService
import no.mabjork.api_gateway.utils.JWTUtil
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import java.net.URI


@Configuration
class GatewayConfig {

    @Bean
    fun myRoutes(builder: RouteLocatorBuilder): RouteLocator {
        return builder
                .routes()
                .route { p ->
                    p.path("/api/user/**")
                            .filters { f ->
                                f.rewritePath("/api/(?<segment>.*)", "/\${segment}")
                                        .hystrix { c -> c.setName("hystrix").fallbackUri = URI("forward:/fallback") }
                            }
                            .uri("lb://${USER_SERVICE}")
                }
                .route { p ->
                    p.path("/api/stock/**")
                            .filters { f ->
                                f.rewritePath("/api/stock/(?<segment>.*)", "/\${segment}")
                                        .hystrix { c -> c.setName("hystrix").fallbackUri = URI("forward:/fallback") }
                            }
                            .uri("lb://${STOCK_SERVICE}")
                }
                .route { p ->
                    p.path("/api/forex/**")
                            .filters { f ->
                                f.rewritePath("/api/forex/(?<segment>.*)", "/\${segment}")
                                        .hystrix { c -> c.setName("hystrix").fallbackUri = URI("forward:/fallback") }
                            }
                            .uri("lb://${FOREX_SERVICE}")
                }
                .route { p ->
                    p.path("/api/prediction/**")
                            .filters { f ->
                                f.rewritePath("/api/prediction/(?<segment>.*)", "/\${segment}")
                                        .hystrix { c -> c.setName("hystrix").fallbackUri = URI("forward:/fallback") }
                            }
                            .uri("lb://${PREDICTION_SERVICE}")
                }
                .build()
    }
    @Bean
    fun tokenFilter(tokenService: TokenService, jwtUtil: JWTUtil): GlobalFilter {
        return GlobalFilter { exchange, chain ->

            val frontendToken = exchange.request.headers[TOKEN_HEADER]?.first()?.replace(TOKEN_PREFIX, "") ?: ""
            val backendToken = tokenService.exchangeToken(frontendToken)

            backendToken.flatMap {
                exchange.request
                        .mutate()
                        .headers { it.add(TOKEN_HEADER, "$TOKEN_PREFIX$backendToken")}


                chain.filter(exchange)
                        .then(Mono.fromRunnable<Void> {
                            val response = exchange.response
                            response.headers.add(TOKEN_HEADER, "$TOKEN_PREFIX$frontendToken")
                            exchange.mutate().response(response)
                        }
                        )
            }
        }
    }
}