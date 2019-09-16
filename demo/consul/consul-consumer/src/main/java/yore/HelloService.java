package yore;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 使用 openfeign 组件，调用远程服务
 *
 * Created by yore on 2019/9/10 19:23
 */
@FeignClient("consul-provider")
public interface HelloService {

    @RequestMapping(value = "/sayHello", method = RequestMethod.GET)
    String sayHello(@RequestParam("name") String name);

}
