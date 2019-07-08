package yore.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * feign调用数据服务
 * Created by yore on 2019/7/7 22:31
 */
@FeignClient(name = "provider-service")
public interface ProviderService {
    @RequestMapping(value = "/getDashboard", method = RequestMethod.GET)
    List<String> getProviderData();
}
