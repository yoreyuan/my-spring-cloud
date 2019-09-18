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
public class RemoteAddrGatewayApp {

    public static void main(String[] args) {
        SpringApplication.run(RemoteAddrGatewayApp.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("before_route", r -> r.remoteAddr("127.0.0.1","192.168.33.9")
                        .uri("https://www.douban.com/"))
                .build();
    }

}
