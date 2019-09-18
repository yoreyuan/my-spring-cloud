package yore.filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * Created by yore on 2019/9/17 21:20
 */
@SpringBootApplication
public class HeaderFilterApp {

    public static void main(String[] args) {
        SpringApplication.run(HeaderFilterApp.class, args);
    }


    @Bean
    public RouteLocator testRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("add_request_header_route", r ->
                        r.path("/test")
                                .filters(f -> f.addRequestHeader("X-Request-Id", "ValueB"))
                                .uri("http://localhost:8011/test/head")
                ).build();
    }

}
