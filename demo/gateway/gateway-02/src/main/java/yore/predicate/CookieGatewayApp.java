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
public class CookieGatewayApp {

    public static void main(String[] args) {
        SpringApplication.run(CookieGatewayApp.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("before_route", r -> r.cookie("chocolate", "ch.p")
                        .uri("http://localhost:8011/test/cookie"))
                .build();
    }

}
