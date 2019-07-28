package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import yore.filter.AuthFilter;

/**
 * Created by yore on 2019/7/27 19:55
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@EnableCircuitBreaker
public class ZuulServerApp {

    public static void main(String[] args) {
        SpringApplication.run(ZuulServerApp.class, args);
    }

    @Bean
    public AuthFilter preRequestFilter() {
        return new AuthFilter();
    }

}
