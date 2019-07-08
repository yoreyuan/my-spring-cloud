package yore.service;

import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;

import java.util.List;

/**
 * Created by yore on 2019/7/7 22:28
 */
public interface IHelloService {
    List<String> getProviderData();

    String hello(Integer id);
    String getUserToCommandKey(@CacheKey Integer id);
    String updateUser(@CacheKey Integer id);
}
