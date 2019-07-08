package yore.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by yore on 2019/7/7 20:20
 */
@FeignClient(name = "feign-provider", fallback = UserServiceFallback.class)
public interface IUserService {

    @RequestMapping(value = "/user/getUser",method = RequestMethod.GET)
    String getUser(@RequestParam("username") String username);

}
