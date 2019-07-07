package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created by yore on 2019/7/6 17:55
 */
@SpringBootApplication
@EnableDiscoveryClient
public class FeignFileServerApp {

    public static void main(String[] args) {
        SpringApplication.run(FeignFileServerApp.class, args);
    }

}
