package no.mabjork.api_gateway.config

import com.netflix.loadbalancer.WeightedResponseTimeRule
import com.netflix.loadbalancer.IRule
import com.netflix.loadbalancer.NoOpPing
import com.netflix.loadbalancer.IPing
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RibbonConfiguration {

    companion object {

    }

    @Bean
    fun ribbonPing(): IPing {
        return NoOpPing()
    }

    @Bean
    fun ribbonRule(): IRule {
        return WeightedResponseTimeRule()
    }
}