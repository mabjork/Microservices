package no.mabjork.ApiGateway.config

import com.netflix.client.config.IClientConfig
import com.netflix.loadbalancer.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RibbonConfiguration{

    @Bean
    fun ribbonPing(): IPing {
        return PingUrl()
    }

    @Bean
    fun ribbonRule(): IRule {
        return AvailabilityFilteringRule()
    }

}