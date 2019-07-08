package yore.service;

import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by yore on 2019/7/7 22:29
 */
@Component
public class HelloService implements IHelloService {
    @Autowired
    private ProviderService dataService;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<String> getProviderData() {
        return dataService.getProviderData();
    }

    @CacheResult
    @HystrixCommand
    public String hello(Integer id) {
        String json = restTemplate.getForObject("http://provider-service/getUser/{1}", String.class, id);
        System.out.println(json);
        return json;
    }


    @CacheResult
    @HystrixCommand(commandKey = "getUser")
    public String getUserToCommandKey(@CacheKey Integer id) {
        String json = restTemplate.getForObject("http://provider-service/getUser/{1}", String.class, id);
        System.out.println(json);
        return json;
    }

    @CacheRemove(commandKey="getUser")
    @HystrixCommand
    public String updateUser(@CacheKey Integer id) {
        System.out.println("删除getUser缓存");
        return "update success";
    }

}
