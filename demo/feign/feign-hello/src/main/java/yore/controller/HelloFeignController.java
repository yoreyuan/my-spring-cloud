package yore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yore.service.HelloFeignService;

/**
 * 如果是 IDEA时，有时注解会提示：Cloud not autowire. No beans of 'HelloFeignService' type found.
 * 解决： IntelliJ IDEA -> Preferences -> Editor Inspections -> Spring -> Spring Core -> Code -> Autowiring for Bean Class -> Severity，
 * 将Error改为Warning，然后Apply Ok。
 *
 * Created by yore on 2019/7/4 20:20
 */
@RestController
public class HelloFeignController {

    @Autowired
    private HelloFeignService helloFeignService;

    // 服务消费者对位提供的服务
    @GetMapping(value = "/search/github")
    public String searchGithubRepoByStr(@RequestParam("str") String queryStr) {
        return helloFeignService.searchRepo(queryStr);
    }

    // 服务消费者对位提供的服务
    @GetMapping(value = "/search2/github")
    public ResponseEntity<byte[]> searchGithubRepoByStr2(@RequestParam("str") String queryStr) {
        return helloFeignService.searchRepo2(queryStr);
    }

}
