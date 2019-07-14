package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * 声明用户admin具有读写权限，用户guest具有读权限，
 *
 * <pre>
 * authenticationManagerBean()  用户手动注入 AuthenticationManager
 * passwordEncoder()        用户声明用户名和密码加密方式
 *
 * 关于这部分更详细的可以查看这个博文 <a href="https://www.cnblogs.com/niechen/p/10150020.html">SpringBoot之oauth2.0学习之服务端配置快速上手</a>
 * </pre>
 * Created by yore on 2019/7/13 10:28
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAuthorizationServer
public class AuthServerApp {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApp.class, args);
    }

}
