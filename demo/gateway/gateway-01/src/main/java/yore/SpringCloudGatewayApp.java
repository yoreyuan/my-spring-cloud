package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * Created by yore on 2019/9/17 20:09
 */
@SpringBootApplication
public class SpringCloudGatewayApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewayApp.class, args);
    }

    /**
     * 基本的转发
     * 当访问http://localhost:8080/jd
     * 转发到http://jd.com
     * @param builder RouteLocatorBuilder
     * @return RouteLocator
     */
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                //basic proxy
//                .route(r ->r.path("/jd")
//                        .uri("http://jd.com:80/").id("jd_route")
//                ).build();
//    }

}
