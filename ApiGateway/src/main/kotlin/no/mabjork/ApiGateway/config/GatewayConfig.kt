package no.mabjork.ApiGateway.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import no.mabjork.ApiGateway.models.AuthUser
import no.mabjork.ApiGateway.service.AuthService
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI


@Configuration
class GatewayConfig {

    companion object {
        const val AUTH_SERVICE = "auth-service"
        const val USER_SERVICE = "user-service"
        const val STOCK_SERVICE = "stock-service"
        const val FOREX_SERVICE = "forex-service"
        const val PREDICTION_SERVICE = "prediction-service"
        const val TOKEN_HEADER = "Authentication"
        const val TOKEN_PREFIX = "Bearer "
    }

    @Bean
    fun myRoutes(builder: RouteLocatorBuilder): RouteLocator {
        return builder
                .routes()
                .route { p ->
                    p.path("/api/auth/**")
                            .filters { f ->
                                f.rewritePath("/api/auth/(?<segment>.*)", "/\${segment}")
                                        .hystrix { c -> c.setName("hystrix").fallbackUri = URI("forward:/fallback") }
                            }
                            .uri("lb://${AUTH_SERVICE}")
                }
                .route { p ->
                    p.path("/api/user/**")
                            .filters { f ->
                                f.rewritePath("/api/user/(?<segment>.*)", "/\${segment}")
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
}