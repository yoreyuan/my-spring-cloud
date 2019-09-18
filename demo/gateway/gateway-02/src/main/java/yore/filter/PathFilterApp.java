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
public class PathFilterApp {

    public static void main(String[] args) {
        SpringApplication.run(PathFilterApp.class, args);
    }


    @Bean
    public RouteLocator testRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("rewritepath_route", r ->
                        r.path("/foo/**")
                                .filters(f -> f.rewritePath("/foo/(?<segment>.*)","/$\\{segment}"))
                                .uri("http://www.baidu.com")
                ).build();
    }

}
