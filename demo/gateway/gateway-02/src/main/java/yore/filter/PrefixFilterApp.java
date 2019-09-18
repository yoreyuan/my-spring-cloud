package yore.filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * 主要在配置文件中设置
 *
 * Created by yore on 2019/9/17 21:20
 */
@SpringBootApplication
public class PrefixFilterApp {

    public static void main(String[] args) {
        SpringApplication.run(PrefixFilterApp.class, args);
    }

}
