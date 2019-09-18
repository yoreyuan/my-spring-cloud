package yore.predicate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * Created by yore on 2019/9/17 20:35
 */
@SpringBootApplication
public class HeaderGatewayApp {

    public static void main(String[] args) {
        SpringApplication.run(HeaderGatewayApp.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("before_route", r -> r.header("X-Request-Id", "Mercury")
                        .uri("http://localhost:8011/test/head"))
                .build();
    }

}
