package yore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * consul-consumer 的启动主类
 *
 * Created by yore on 2019/9/10 19:24
 */
@RestController
@SpringBootApplication
@EnableFeignClients
public class ConsulConsumerApp {

    /** 调用 hello 服务*/
    @Autowired
    private HelloService helloService;

    @GetMapping("/actuator/health")
    public String health(){
        return "SUCCESS";
    }

    /** 接收前端传来的参数，调用远程接口，并返回调用结果 */
    @GetMapping("/hello")
    public String hello(String name){
        System.out.println("**********" + name);
        return helloService.sayHello(name);
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsulConsumerApp.class,args);
    }


}
