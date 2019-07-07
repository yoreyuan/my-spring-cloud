package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created by yore on 2019/7/7 18:59
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ClientAApp {

    public static void main(String[] args) {
        SpringApplication.run(ClientAApp.class, args);
    }

}
