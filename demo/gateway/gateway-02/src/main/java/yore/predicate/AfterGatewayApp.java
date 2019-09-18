package yore.predicate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 *
 *
 * Created by yore on 2019/9/17 20:05
 */
@SpringBootApplication
public class AfterGatewayApp {

    public static void main(String[] args) {
        SpringApplication.run(AfterGatewayApp.class, args);
    }


    /**
     * 也可以通过配置文件形式
     *
     * 其中 - After时间可以使用 UtcTimeUtil工具类生成
     *
     * @param builder RouteLocatorBuilder
     * @return RouteLocator
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        //生成比当前时间早一个小时的UTC时间。 minusHours减去指定的小时数
//        ZonedDateTime minusTime = LocalDateTime.now().minusHours(1).atZone(ZoneId.systemDefault());
        ZonedDateTime minusTime = LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault());
        return builder.routes()
                .route("after_route", r -> r.after(minusTime)
                        .uri("http://baidu.com"))
                .build();
    }

}
