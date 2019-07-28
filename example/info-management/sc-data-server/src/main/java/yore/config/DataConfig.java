package yore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置信息
 *
 * Created by yore on 2019/7/27 20:46
 */
@Component
@ConfigurationProperties(prefix = "yore")
public class DataConfig {

    // 通过配置中心获取的 配置项 yore.defaultUser 值 （yore-dev）
    private String defaultUser;

    public String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(String defaultUser) {
        this.defaultUser = defaultUser;
    }

}
