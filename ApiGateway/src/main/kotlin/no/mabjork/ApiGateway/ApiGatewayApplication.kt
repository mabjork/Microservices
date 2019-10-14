package no.mabjork.ApiGateway


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI

@SpringBootApplication
@EnableEurekaClient
class ApiGatewayApplication

fun main(args: Array<String>) {
	runApplication<ApiGatewayApplication>(*args)
}

@Bean
fun myRoutes(builder: RouteLocatorBuilder, filter: RouteFilter): RouteLocator {
	return builder
			.routes()
			.route { p -> p.path("/api/**")
					.filters { f -> f.rewritePath("/api/(?.*)", "/\${remains}").filter(filter)
							.hystrix { c -> c.setName("hystrix").fallbackUri = URI("forward:/fallback")
					} }
					.uri("no://op") }
			.build()
}

@Bean
fun routeFilter(discoveryClient: LoadBalancerClient): RouteFilter = RouteFilter(discoveryClient)


class RouteFilter(val discoveryClient: LoadBalancerClient) : GatewayFilter, Ordered {

	override fun filter(exchange: ServerWebExchange?, chain: GatewayFilterChain?): Mono<Void> {

		if(exchange == null) return Mono.empty()

		if(chain == null) return Mono.empty()

		val uri = exchange?.request?.headers?.host.toString()

		val serviceId = uri.split("/")[0]

		val instance = discoveryClient.choose(serviceId)

		val baseUrl = instance.uri.toString()

		val newUrl = baseUrl + uri.replace(serviceId, "")

		exchange.attributes.put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, URI(newUrl))

		return chain.filter(exchange)
	}

	override fun getOrder(): Int {
		return RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER + 1;
	}

}
