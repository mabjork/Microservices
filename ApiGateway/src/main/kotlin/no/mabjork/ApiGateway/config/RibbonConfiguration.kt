package no.mabjork.ApiGateway.config

import com.netflix.loadbalancer.WeightedResponseTimeRule
import com.netflix.loadbalancer.IRule
import com.netflix.loadbalancer.NoOpPing
import com.netflix.loadbalancer.IPing
import org.springframework.context.annotation.Bean


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