package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created by yore on 2019/7/4 20:04
 */
@SpringBootApplication
@EnableFeignClients
public class HelloFeignApp {

    public static void main(String[] args) {
        SpringApplication.run(HelloFeignApp.class, args);
    }

//    @Bean
//    Logger.Level feignLoggerLevel(){
//        return Logger.Level.FULL;
//    }

}
