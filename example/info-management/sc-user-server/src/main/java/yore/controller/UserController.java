package yore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import yore.service.IUserService;

/**
 * Created by yore on 2019/7/27 19:56
 */
@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 获取配置文件中系统默认用户
     * @return
     */
    @GetMapping("/getDefaultUser")
    public String getDefaultUser(){
        return userService.getDefaultUser();
    }

    /**
     * 获取上下文用户
     * @return
     */
    @GetMapping("/getContextUserId")
    public String getContextUserId(){
        return userService.getContextUserId();
    }

    /**
     * 获取供应商数据
     * @return
     */
    @GetMapping("/getProviderData")
    public List<String> getProviderData(){
        return userService.getProviderData();
    }

}
