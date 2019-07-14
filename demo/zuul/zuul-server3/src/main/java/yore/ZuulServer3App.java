package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import yore.filter.GrayFilter;

/**
 * Created by yore on 2019/7/13 22:04
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class ZuulServer3App {

    public static void main(String[] args) {
        SpringApplication.run(ZuulServer3App.class, args);
    }

//    @Bean
//    public GrayFilter grayFilter(){
//        return new GrayFilter();
//    }

}
