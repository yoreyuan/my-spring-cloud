package yore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * consul-config 启动类主类
 *
 * Created by yore on 2019/9/10 19:27
 */
@SpringBootApplication
@RestController
//自动刷新
@RefreshScope
public class ConsulConfigApp {
    public static void main(String[] args) {
        SpringApplication.run(ConsulConfigApp.class,args);
    }

    // 读取远程配置
    @Value("${foo.bar.name}")
    private String name;

    // 将配置展示在页面
    @GetMapping("/getName")
    public String getName(){
        return name;
    }

}
