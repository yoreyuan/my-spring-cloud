package yore.controller;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yore on 2019/7/7 19:00
 */
@Api(description = "测试源服务API接口")
@RestController
public class TestController {

    @ApiOperation(value = "加法", notes = "带标记加法")
    @GetMapping("/add")
    public String add(Integer a, Integer b, HttpServletRequest request){
        return " From Port: "+ request.getServerPort() + ", Result: " + (a + b);
    }

    @ApiOperation(value = "加法", notes = "加法")
    @GetMapping("/a/add")
    public Integer aadd(Integer a, Integer b){
        return a + b;
    }

    @ApiOperation(value = "减法", notes = "减法")
    @GetMapping("/sub")
    public Integer sub(Integer a, Integer b){
        return a - b;
    }

    @ApiOperation(value = "乘法", notes = "乘法")
    @GetMapping("/mul")
    public String mul(Integer a, Integer b){
        System.out.println("进入client-a!");
        return "client-a-" + a * b;
    }

    @ApiOperation(value = "除法", notes = "除法")
    @GetMapping("/div")
    public Integer div(Integer a, Integer b){
        return a / b;
    }

}
