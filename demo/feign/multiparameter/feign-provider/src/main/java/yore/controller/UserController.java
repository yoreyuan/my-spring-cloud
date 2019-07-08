package yore.controller;

import org.springframework.web.bind.annotation.*;
import yore.entity.User;

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

    @RequestMapping(value = "/getUser",method = RequestMethod.GET)
    public String getUser(@RequestParam("username") String username) throws Exception{
        if(username.equals("spring")) {
            return "This is real user";
        }else {
            throw new Exception();
        }
    }

}
