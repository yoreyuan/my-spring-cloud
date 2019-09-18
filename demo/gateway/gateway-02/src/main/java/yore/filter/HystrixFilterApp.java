package yore.filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

/**
 * Created by yore on 2019/9/17 21:31
 */
@SpringBootApplication
public class HystrixFilterApp {

    public static void main(String[] args) {
        SpringApplication.run(HystrixFilterApp.class, args);
    }

}
