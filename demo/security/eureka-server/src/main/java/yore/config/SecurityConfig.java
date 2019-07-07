package yore.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * spring-boot-starter-security默认开启了csrf校验，对于Client端这类费界面应用不合适，但有没有配置方式来禁止。
 * 所以需要定义这个类，来关闭这个校验。
 *
 * Created by yore on 2019/7/3 21:25
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter  {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.csrf().disable();
    }
}
