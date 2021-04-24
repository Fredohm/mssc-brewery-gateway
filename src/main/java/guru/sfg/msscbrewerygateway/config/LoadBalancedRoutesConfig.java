package guru.sfg.msscbrewerygateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local-discovery")
@Configuration
public class LoadBalancedRoutesConfig {

    @Bean
    public RouteLocator loadBalancedRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/v1/beer*", "/api/v1/beer/*", "/api/v1/beerUpc/*")
                        //.uri("lb://beer-service"))
                        .uri("http://localhost:8080"))
                .route(r -> r.path("/api/v1/customers/**")
                        //.uri("lb://beer-order-service"))
                        .uri("http://localhost:8081"))
                .route(r -> r.path("/api/v1/beer/*/inventory")
                        .filters(f -> f.circuitBreaker(c -> c.setName("inventoryCB")
                                .setFallbackUri("forward:/inventory-failover")
                                .setRouteId("inv-failover")
                        ))
                        //.uri("lb://beer-inventory-service"))
                        .uri("http://localhost:8082"))
                .route(r -> r.path("/inventory-failover/**")
                        //.uri("lb://inventory-failover"))
                        .uri("http://localhost:8083"))
                .build();
    }
}
