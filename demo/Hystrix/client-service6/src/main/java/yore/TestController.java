package yore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yore.service.IUserService;

/**
 * Created by yore on 2019/7/8 00:00
 */
@RestController
public class TestController {

    @Autowired
    private IUserService userService;

    @GetMapping("/getUser")
    public String getUser(@RequestParam String username) throws Exception{
        return userService.getUser(username);
    }

}
