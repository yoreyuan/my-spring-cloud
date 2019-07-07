package yore.controller;

import yore.entity.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * GET或POST请求多参数传递的服务体统者
 *
 * Created by yore on 2019/7/6 15:15
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addUser(User user , HttpServletRequest request){
        String token=request.getHeader("oauthToken");
        return "hello,"+user.getName();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateUser( @RequestBody User user){
        return "hello,"+user.getName();
    }

}
