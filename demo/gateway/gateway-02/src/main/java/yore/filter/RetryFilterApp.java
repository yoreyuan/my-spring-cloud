package yore.filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

/**
 * Created by yore on 2019/9/17 21:20
 */
@SpringBootApplication
public class RetryFilterApp {

    public static void main(String[] args) {
        SpringApplication.run(RetryFilterApp.class, args);
    }


    @Bean
    public RouteLocator testRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("retry_route", r -> r.path("/test/retry")
                        .filters(f ->f.retry(config -> config.setRetries(2).setStatuses(HttpStatus.INTERNAL_SERVER_ERROR)))
                        .uri("http://localhost:8011/retry?key=abc&count=2"))
                .build();
    }

}
