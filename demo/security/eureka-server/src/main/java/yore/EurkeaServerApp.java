package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Created by yore on 2019/7/3 21:24
 */
@SpringBootApplication
@EnableEurekaServer
public class EurkeaServerApp {

    public static void main(String[] args) {
        SpringApplication.run(EurkeaServerApp.class, args);
    }

}
