package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 数据服务
 *
 * Created by yore on 2019/7/27 20:43
 */
@SpringBootApplication
@EnableDiscoveryClient
//启动断路器
@EnableCircuitBreaker
public class DataServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(DataServiceApp.class, args);
    }

}
