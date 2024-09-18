package com.indium.iplapigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RoutingConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("insert-match-data", r -> r.path("/api/matches/insert")
                        .uri("http://localhost:9000"))  // Forward to cricket microservice
                .route("get-matches-by-player", r -> r.path("/api/matches/player/**")
                        .uri("http://localhost:9000"))  // Forward to cricket microservice
                .route("get-cumulative-score", r -> r.path("/api/matches/cumulative-score")
                        .uri("http://localhost:9000"))  // Forward to cricket microservice
                .route("get-scores-by-date", r -> r.path("/api/matches/score")
                        .uri("http://localhost:9000"))  // Forward to cricket microservice
                .route("get-top-batsmen", r -> r.path("/api/matches/topbatsmen")
                        .uri("http://localhost:9000"))  // Forward to cricket microservice
                .build();
    }
}
//
//package com.indium.iplapigateway;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Configuration
//public class RoutingConfiguration {
//
//    @Autowired
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("insert-match-data", r -> r.path("/api/matches/insert")
//                        .filters(f -> f.filter(jwtAuthenticationFilter))
//                        .uri("http://localhost:9000"))  // Forward to cricket microservice
//                .route("get-matches-by-player", r -> r.path("/api/matches/player/**")
//                        .filters(f -> f.filter(jwtAuthenticationFilter))
//                        .uri("http://localhost:9000"))  // Forward to cricket microservice
//                .route("get-cumulative-score", r -> r.path("/api/matches/cumulative-score")
//                        .filters(f -> f.filter(jwtAuthenticationFilter))
//                        .uri("http://localhost:9000"))  // Forward to cricket microservice
//                .route("get-scores-by-date", r -> r.path("/api/matches/score")
//                        .filters(f -> f.filter(jwtAuthenticationFilter))
//                        .uri("http://localhost:9000"))  // Forward to cricket microservice
//                .route("get-top-batsmen", r -> r.path("/api/matches/topbatsmen")
//                        .filters(f -> f.filter(jwtAuthenticationFilter))
//                        .uri("http://localhost:9000"))  // Forward to cricket microservice
//                .build();
//    }
//}