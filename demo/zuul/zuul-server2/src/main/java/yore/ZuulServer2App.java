package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * Created by yore on 2019/7/13 07:06
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@EnableOAuth2Sso
//@EnableResourceServer
//@EnableGlobalMethodSecurity(prePostEnabled=true)
public class ZuulServer2App /*extends WebSecurityConfigurerAdapter*/ extends ResourceServerConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(ZuulServer2App.class, args);
    }

    /**
     * Groovy加载方法配置，20秒自动刷新
     */
//    @Component
//    public static class GroovyRunner implements CommandLineRunner {
//        @Override
//        public void run(String... args) throws Exception {
//            MonitoringHelper.initMocks();
//            FilterLoader.getInstance().setCompiler(new GroovyCompiler());
//            try {
//                FilterFileManager.setFilenameFilter(new GroovyFileFilter());
//                // 使用绝对路径，每隔20秒扫描一次
//                FilterFileManager.init(20, "/Users/yoreyuan/soft/idea_workspace/work2/spring/my-spring-cloud/demo/zuul/zuul-server2/src/main/java/yore/filter/groovy");
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }

    /**
     * 资源认证服务
     *
     * 请求的这些路径，是允许直接访问的，否则必须携带access_token才能访问
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login", "/client/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf()
                .disable();
    }

}
