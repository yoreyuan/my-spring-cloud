package yore.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * feign调用数据服务
 *
 * Created by yore on 2019/7/27 19:00
 */
@FeignClient(name = "sc-data-service", fallback=UserClientFallback.class)
public interface DataService {

    @RequestMapping(value = "/getDefaultUser", method = RequestMethod.GET)
    String getDefaultUser();

    @RequestMapping(value = "/getContextUserId", method = RequestMethod.GET)
    String getContextUserId();

}
