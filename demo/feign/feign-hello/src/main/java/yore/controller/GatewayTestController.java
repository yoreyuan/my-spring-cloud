package yore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yore on 2019/9/17 20:57
 */
@RestController
public class GatewayTestController {
    private final Logger log = LoggerFactory.getLogger(GatewayTestController.class);
    ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<>();

    /**
     * 测试Cookies路由断言工厂
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return String
     */
    @GetMapping("/test/cookie")
    public String testGateway(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName()+":"+cookie.getValue());
            }
        }
        return "Spring Cloud Gateway,Hello world!";
    }


    /**
     * 测试Head路由断言工厂
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return String
     */
    @GetMapping("/test/head")
    public String testGatewayHead(HttpServletRequest request, HttpServletResponse response){
        String head=request.getHeader("X-Request-Id");
        return "return head info:"+head;
    }


    @GetMapping("/test/addRequestParameter")
    public String addRequestParameter(HttpServletRequest request, HttpServletResponse response){
        String parameter=request.getParameter("example");
        return "return addRequestParameter info:"+parameter;
    }


    /**
     * 模拟一个请求异常的处理
     * @return String
     */
    @GetMapping("/retry")
    public String testRetryByException(@RequestParam("key") String key, @RequestParam(name = "count") int count) {
        /*
         * computeIfAbsent 如果指定的key没有与值关联的，则尝试使用给定的映射函数计算其值，并将其输入此映射，除非为null
         * 整个方法调用时以原子方式执行的，因此每个key最多因公一次该函数。
         */
        AtomicInteger num = map.computeIfAbsent(key, s -> new AtomicInteger());
        //对请求或重试次数计数
        int i = num.incrementAndGet();
        log.warn("重试次数: "+i);
        //计数i小于重试次数2抛出异常，让Spring Cloud Gateway进行重试
        if (i < count) {
            throw new RuntimeException("Deal with failure, please try again!");
        }
        //当重试两次时候，清空计数，返回重试两次成功
        map.clear();
        return "重试"+count+"次成功！";
    }


    @GetMapping("/test/Hystrix")
    public String index(@RequestParam("isSleep") boolean isSleep) throws InterruptedException {
        log.info("issleep is " + isSleep);
        //isSleep为true开始睡眠，睡眠时间大于Gateway中的fallback设置的时间(5s)。最短睡眠时间为10s，如果小于或等于零，则根本需要😪
        if (isSleep) {
            TimeUnit.MINUTES.sleep(10);
        }
        return "No Sleep";
    }

}
