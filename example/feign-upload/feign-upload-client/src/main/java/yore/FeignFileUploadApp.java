package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created by yore on 2019/7/6 15:55
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class FeignFileUploadApp {

    public static void main(String[] args) {
        SpringApplication.run(FeignFileUploadApp.class, args);
    }
}
