package yore.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Component;

/**
 * Created by yore on 2019/7/7 22:57
 */
@Component
public class UserService implements IUserService{

    @Override
    @HystrixCommand(fallbackMethod="defaultUser")
    public String getUser(String username) throws Exception {
        if(username.equals("spring")) {
            return "This is real user";
        }else {
            throw new Exception();
        }
    }

    /**
     * 出错则调用该方法返回友好错误
     * @param username username
     * @return String
     */
    public String defaultUser(String username) {
        return "The user does not exist in this system";
    }

}
