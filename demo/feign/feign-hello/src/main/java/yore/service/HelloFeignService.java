package yore.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import yore.config.HelloFeignServiceConfig;

/**
 * 通过FeignClient注解手动指定RUL，改调用地址用于根据传入字符搜索GitHub上相关的仓库信息。
 * HelloFeignService会根据RequestMapping对应的方法，转换成最终的请求地址：https://api.github.com/search/repositories?q=queryStr
 *
 * Created by yore on 2019/7/4 20:10
 */
@FeignClient(name = "github-client", url = "https://api.github.com", configuration = HelloFeignServiceConfig.class)
public interface HelloFeignService {

    /**
     * content: {"message":"Validation Failed","errors":[{"resource":"Search","field":"q","code":"missing"}],
     * "documentation_url":"https://developer.github.com/v3/search"}
     * @param queryStr 参数值
     * @return String
     */
    @RequestMapping(value = "/search/repositories", method = RequestMethod.GET)
    String searchRepo(@RequestParam("q") String queryStr);

    @RequestMapping(value = "/search/repositories", method = RequestMethod.GET)
    ResponseEntity<byte[]> searchRepo2(@RequestParam("q") String queryStr);

}
