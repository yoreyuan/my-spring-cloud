package yore.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import yore.entity.User;
import yore.service.UserFeignService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yore on 2019/7/6 15:34
 */
@RestController
@RequestMapping("/user")
@Api(tags = "user-controller")
public class UserController {

    @Autowired
    private UserFeignService userFeignService;

    /**
     * 用于演示Feign的Get请求多参数传递
     * @param user 用户对象
     * @return String
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value="post方式添加用户的方法", notes = "test")
    public String addUser( @RequestBody @ApiParam(name="用户",value="传入json格式",required=true) User user){
        return userFeignService.addUser(user);
    }

    /**
     * 用于演示Feign的Post请求多参数传递
     * @param user 用户对象
     * @return String
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value="post方式update用户的方法", notes = "test")
    public String updateUser( @RequestBody @ApiParam(name="用户",value="传入json格式",required=true) User user){
        return userFeignService.updateUser(user);
    }

}
