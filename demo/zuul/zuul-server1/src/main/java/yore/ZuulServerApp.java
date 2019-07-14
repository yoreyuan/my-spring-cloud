package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Created by yore on 2019/7/7 12:10
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
//@EnableZuulServer
public class ZuulServerApp {

    public static void main(String[] args) {
        SpringApplication.run(ZuulServerApp.class, args);

    }
}
