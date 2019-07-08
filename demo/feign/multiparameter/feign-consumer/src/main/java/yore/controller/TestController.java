package yore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yore.service.IUserService;

/**
 * Created by yore on 2019/7/7 20:29
 */
@RestController
public class TestController {

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/getUser",method = RequestMethod.GET)
    public String getUser(@RequestParam("username") String username) throws Exception{
        return userService.getUser(username);
    }

}
