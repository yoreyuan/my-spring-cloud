package yore.service;

import org.springframework.stereotype.Component;

/**
 * Created by yore on 2019/7/8 00:21
 */
@Component
public class UserServiceFallback  implements IUserService {
    /**
     * 出错则调用该方法返回友好错误
     * @param username username
     * @return String
     */
    public String getUser(String username){
        return "The user does not exist in this system, please confirm username";
    }

}
