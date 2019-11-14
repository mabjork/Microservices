package no.mabjork.api_gateway.config

import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfig {

    companion object {
        const val AUTH_SERVICE = "auth-service"
        const val USER_SERVICE = "user-service"
        const val STOCK_SERVICE = "stock-service"
        const val FOREX_SERVICE = "forex-service"
        const val PREDICTION_SERVICE = "prediction-service"
        const val TOKEN_HEADER = "Authentication"
        const val TOKEN_PREFIX = "Bearer "
    }

}