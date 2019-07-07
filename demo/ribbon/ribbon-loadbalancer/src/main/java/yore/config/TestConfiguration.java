package yore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;

/**
 * ResponseTimeWeightedRule已过期，使用：WeightedResponseTimeRule
 *
 * 其他的策略可以查看：com.netflix.ribbon:ribbon-loadbalancer:2.2.5.jar 包下的
 * com.netflix.loadbalancer
 *
 * Created by yore on 2019/7/7 19:46
 */
@Configuration
@AvoidScan
public class TestConfiguration {

//    @Bean
//    public IRule ribbonRule() {
////        return new WeightedResponseTimeRule();
//        return new RandomRule();
//    }

    @Autowired
    IClientConfig config;

    @Bean
    public IRule ribbonRule(IClientConfig config) {
        return new RandomRule();
    }

}
