package yore.service;

import org.springframework.stereotype.Component;

/**
 * Created by yore on 2019/7/27 20:00
 */
@Component
public class UserClientFallback implements DataService{

    @Override
    public String getDefaultUser() {
        return new String("get getDefaultUser failed");
    }
    @Override
    public String getContextUserId() {
        return new String("get getContextUserId failed");
    }

}
