Spring Cloud Gateway
---

Spring Cloud生态体系中的第二代网关 —— [Spring Cloud Gateway](https://github.com/spring-cloud/spring-cloud-gateway)。

# 1 概述
## 1.1 什么是Spring Cloud Gateway
它是Spring官方基于Spring 5.0、Spring Boot 2.0和Project Reactor等技术开发的网关，旨在为微服务架构提供简单、有效且统一的API路由管理方式。
Spring Cloud Gateway 作为 Spring Cloud 生态系统中的网关，目标是替代Netflix Zuul，其不仅提供统一的路由方式，
并且还基于Filter链的方式提供了网关的基本功能，例如：安全、监控/埋点、限流等。

## 1.2 Spring Cloud Gateway 的核心概念
一般来说网关对外暴露的URL或者接口信息，我们统称为路由信息。

* **路由（Route）**：路由是网关最重要的基础部分，路由信息由一个ID、一个目的url、一组断言工厂和一组Filter组成。如路由断言为真，则说明请求的url和配置的路由匹配。
* **断言（Predicate）**：Java 8中的断言函数。Spring Cloud Gateway中断言函数输入类型是Spring 5.0框架中的ServerWebExchange。
Spring Cloud Gateway中的断言函数允许开发这去定义匹配来自于Http Request中的任何信息，比如请求头和参数。
* **过滤器（Filter）**：一个标准的Spring webFilter。Spring Cloud Gateway中的Filter分为两种类型的Filter，分别是**Gateway Filter**和**Global Filter**，过滤器Filter将会对请求和响应进行进行修改处理


# 2 工作原理
Gateway的客户端回想Spring Cloud Gateway发起请求，请求首先会被`HttpWebHandlerAdapter`进行提取组装成网关的上下文，然后网关的上下文会传递到`DispatcherHandle`。
DispatcherHandle是所有请求的分发处理器，其主要负责分发请求对应的处理器。
比如，将请求分发到对应`RoutePredicateHandlerMapping`，路由断言处理映射器主要用于路由的查找，以及找到路由后返回对应的`FilteringWebHandler`，
FilteringWebHandler主要负责组装Filter链表并调用Filter执行一系列的Filter处理，然后把请求转到后端对应的代理服务处理，处理完毕之后，将Response返回到Gateway客户端。

过滤器可以在转发请求之前处理或者接收到被代理服务的返回结果之后处理。所有的Pre类型的Filter执行完毕之后，才会转发请求到被代理的服务器处理。被代理的服务把所有请求处理完毕之后，才会执行Post类型过滤器。


# 3 入门案例
作为网关来说，网关最重要的功能就是**协议适配**和**协议转发**，协议转发也就是基本的路由信息转发

如果报如下错误：
```

***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of method modifyRequestBodyGatewayFilterFactory in org.springframework.cloud.gateway.config.GatewayAutoConfiguration required a bean of type 'org.springframework.http.codec.ServerCodecConfigurer' that could not be found.


Action:

Consider defining a bean of type 'org.springframework.http.codec.ServerCodecConfigurer' in your configuration.

```
依赖中去除`spring-boot-starter-web`依赖

* 启动后访问 [http://localhost:8080/actuator/gateway/routes]()，可以获取如下路由信息
```json
[
  {
    "route_id": "jd_route",
    "route_object": {
      "predicate": "org.springframework.cloud.gateway.support.ServerWebExchangeUtils$$Lambda$330/598977164@2f515457"
    },
    "order": 0
  }
]
```

* 其他的端点信息可以通过查看 Gateway的源码 `org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint.java`源码
* 浏览器访问 [http://localhost:8080/jd]()，可以看到转发到了京东首页

* 使用配置文件方式配置
```yaml
spring:
  cloud:
    gateway:
      routes: #当访问http://localhost:8080/baidu,直接转发到https://www.baidu.com/
      - id: baidu_route
        uri: http://baidu.com:80/
        predicates:
        - Path=/baidu
```
* 注释掉`customRouteLocator`方法，重启项目，访问[http://localhost:8080/baidu]()，可以看到转发到了百度首页


# 4 路由断言
Spring Cloud Gateway的路由匹配的功能是以Spring WebFlux中的 Handler Mapping 为基础实现的。Spring Cloud Gateway也是由许多的路由断言工厂组成的。
当Http Request请求进如Spring Cloud Gateway的时候，网关中的路由断言工厂会根据配置的路由规则，对Http Request请求进行断言匹配。
匹配成功则记性下一步处理，否则断言失败直接返回错误信息。

## 4.1 After 路由断言工厂
After Route Predicate Factory中会取出一个UTC时间格式的时间参数，当请求进来的当前时间在配置的UTC时间之后，则会成功匹配，否则不能成功匹配。

* 配置文件可以如下配置
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: after_route
        uri: http://baidu.com
        predicates:
        - After=2019-09-17T19:11:59.683+08:00[Asia/Shanghai] 
```
* 启动[gateway-02](../demo/gateway/gateway-02/src/main/java/yore/predicate/AfterGatewayApp.java)，
* 访问 [http://localhost:8081](#)，可以转发到百度首页，

* 将时间修改为比当前时间大的，再次访问，发现报404

## 4.2 Before 路由断言工厂
当请求进来的当前时间在路由断言工厂之前会成功匹配，否则不能成功匹配

* 配置文件可以如下配置
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: before_route
        uri: http://baidu.com
        predicates:
        - Before=2019-09-17T19:11:59.683+08:00[Asia/Shanghai] 
```
* 启动[gateway-02](../demo/gateway/gateway-02/src/main/java/yore/predicate/BeforeGatewayApp.java)，
* 访问 [http://localhost:8081](#)，可以转发到百度首页

## 4.3 Between 路由断言工厂
当请求进来的当前时间在配置的UTC时间工厂之间会成功匹配，否则不能成功匹配

* 配置文件可以如下配置
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: between_route
        uri: http://baidu.com
        predicates:
        - name: Between
          args:
            datetime1: 2019-09-16T19:11:59.683+08:00[Asia/Shanghai] 
            datetime2: 2019-09-17T20:11:59.683+08:00[Asia/Shanghai]
```
* 启动[gateway-02](../demo/gateway/gateway-02/src/main/java/yore/predicate/BetweenGatewayApp.java)，
* 访问 [http://localhost:8081](#)，可以转发到百度首页
```html
<html>
<meta http-equiv="refresh" content="0;url=http://www.baidu.com/">
</html>
```

## 4.4 Cookie 路由断言工厂
Cookie断言工厂会取两个参数——Cookie名称对应key和value。当请求中携带的cookie和Cookied断言工厂中配置的cookie一致，则路由成功，否则匹配不成功。

* 在项目[feign-hello](../demo/feign/feign-hello/src/main/java/yore/controller/GatewayTestController.java)添加测试接口方法，然后启动项目
* 配置文件可以如下配置
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: cookie_route
        uri: http://localhost:8011/test/cookie
        predicates:
        - Cookie=chocolate,ch.p
```
* 使用postman设置Headers，KEY:Cookie，VALUE:chocolate=ch.p，get请求[http://localhost:8081](#)，
成功会返回`Spring Cloud Gateway,Hello world!`，未匹配会返回404.

## 4.5 Header 路由断言工厂
根据配置的路由header信息进行断言匹配路由，匹配成功则进行转发，否则不进行转发。

* 在项目[feign-hello](../demo/feign/feign-hello/src/main/java/yore/controller/GatewayTestController.java)添加测试接口方法，然后启动项目
* 配置文件可以如下配置
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: header_route
        uri: http://localhost:8011/test/head
        predicates:
        - Header=X-Request-Id, Mercury
```
* 使用postman设置Headers，KEY:X-Request-Id，VALUE:Mercury，get请求[http://localhost:8081](#)，
成功会返回`return head info:Mercury`，未匹配会返回404.

## 4.6 Host 路由断言工厂
根据配置的Host，对请求中的Host进行断言处理，断言成功则进行路由转发，否则不转发
* 配置文件可以如下配置
```yaml
# 如果端口是80，则需把80端口省去
spring:
  cloud:
    gateway:
      routes:
      - id: host_route
        uri: http://jd.com
        predicates:
        - Host=www.**.com:8081
```
* 配置本地的host，将127.0.0.1映射为www.myself.com
* 访问 [http://www.myself.com:8081](#)

## 4.7 Method 路由断言工厂
根据路由信息配置的method对请求方法是Get或者Post等进行断言匹配，匹配成功则进行转发，否则处理失败。

* 配置文件可以如下配置
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: method_route
        uri: http://jd.com
        predicates:
        - Method=GET
```
* 启动项目，直接访问 [http://localhost:8081](#)

## 4.8 Query 路由断言工厂
从请求中获取两个参数，将请求中参数和Query断言路由中的配置进行匹配，比如`http://localhost:8081/?foo=baz`中的foo=baz和下面的`r.query("foo","baz")配置一直则转发成功，否则转发失败。

* 配置文件可以如下配置
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: query_route
        uri: https://www.douban.com/
        predicates:
        - Query=foo,baz
```
* 启动项目，直接访问 [http://localhost:8081/?foo=baz](#)，如果成功则会转发到豆瓣官网

## 4.9 RemoteAddr 路由断言工厂
RemoteAddr路由断言工厂配置一个IPV4或者IPV6网段的字符串或者IP。当请求IP地址在网段之内或者和配置的IP相同，则表示匹配成功，成功转发，否则不能转发。
例如`192.168.33.1/16`是一个网段，当然也可以是一个ip地址

* 配置文件可以如下配置
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: remoteaddr_route
        uri: https://www.douban.com/
        predicates:
        - RemoteAddr=127.0.0.1,192.168.33.9
```
* 启动项目，直接访问 [http://127.0.0.1:8081](#)，如果成功则会转发到豆瓣官网


# 5 Spring Cloud Gateway 的内置Filter
Spring Cloud Gateway提供了很多种类的过滤器工厂，过滤器的实现类将近二十多个，总的来说，可以分为七类：
* Header
* Parameter
* Path
* Status
* Redirect跳转
* Hystrix熔断
* RateLimiter

## 5.1 AddRequestHeader 过滤器工厂
用于对匹配上的请求加上header

* 启动项目：[HelloFeignApp](../demo/feign/feign-hello/src/main/java/yore/HelloFeignApp.java)
* 启动项目：[HeaderFilterApp](../demo/gateway/gateway-02/src/main/java/yore/filter/HeaderFilterApp.java)
* 使用postman设置Headers，KEY:X-Request-Acme，VALUE:ValueB，get请求[http://localhost:8081/test](#)，
成功会返回`return head info:ValueB`，未匹配会返回404.

## 5.2 AddRequestParameter 过滤器
匹配上的请求路由添加请求参数

* 启动项目：[HelloFeignApp](../demo/feign/feign-hello/src/main/java/yore/HelloFeignApp.java)
* 启动项目：[ParamterFilterApp](../demo/gateway/gateway-02/src/main/java/yore/filter/ParamterFilterApp.java)
* 使用postman设置Headers，KEY:example，VALUE:ValueB，get请求[http://localhost:8081/test](#)，
成功会返回`return addRequestParameter info:ValueB`，未匹配会返回404.

## 5.3 RewritePath 过滤器工厂
重写路径

* 启动项目：[PathFilterApp](../demo/gateway/gateway-02/src/main/java/yore/filter/PathFilterApp.java)
* 访问： [http://localhost:8081/foo/cache/sethelp/help.html](#)。这里相当于把foo前缀去掉，
直接访问 [https://www.baidu.com/cache/sethelp/help.html](https://www.baidu.com/cache/sethelp/help.html)


## 5.4 AddResponseHeader 过滤器
从网关返回的响应添加Header

* 启动项目：[ResponseHeaderFilterApp](../demo/gateway/gateway-02/src/main/java/yore/filter/ResponseHeaderFilterApp.java)
* 访问： [http://localhost:8081/test](#5.4)，在浏览器打开**开发者工具**，在Network中点击test，查看其Headers信息，可以看到在Response Headers中添加的响应头信息

## 5.5 StripPrefix 过滤器
`StripPrefixGatewayFilterFactory` 针对请求url前缀进行处理的filter工厂，用于去除前缀
`PrefixPathGatewayFilterFactory` 用于增加前缀

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: baidu_route
        uri: http://www.baidu.com
        predicates:
        - Path=/baidu/test/**
        filters:
        - StripPrefix=2
```
* 启动项目：[PrefixFilterApp](../demo/gateway/gateway-02/src/main/java/yore/filter/PrefixFilterApp.java)
* 访问： [http://localhost:8081/baidu/test](#5.5)。可以看到直接去掉了`/baidu/test`，转到了 https://www.baidu.com/

## 5.6 Retry 过滤器
如果出现异常或者网络抖动，为了保证后端服务的高可用，一般处理方式会对网络请求进行重试。

* 启动项目：[HelloFeignApp](../demo/feign/feign-hello/src/main/java/yore/HelloFeignApp.java)
* 启动项目：[RetryFilterApp](../demo/gateway/gateway-02/src/main/java/yore/filter/RetryFilterApp.java)
* 访问： [http://localhost:8081/test/retry](#)。 浏览器返回`重试2次成功！`信息，并且查看服务端(HelloFeignApp)控制台有异常日志抛出，并进行了两次重试

## 5.7 Hystrix 过滤器
Hystrix可以提供熔断、自我保护、服务降级和快速失败等功能。 Spring Cloud Gateway对Hystrix进行了集成提供路由层面的服务熔断和降级，
最简单的使用场景就是当通过Spring Cloud Gateway调用后端服务，后端服务一直出现异常、服务不可用的状态，此时为了提高用户体验，
就需要对服务降级，返回友好的提示信息，在保护网关自身可用的同时保护后端服务高可用。

* 引入依赖
```xml
<!-- hystrix的Starter-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

* 修改配置文件
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: hystrix_route
        uri: http://localhost:8081/test/Hystrix?isSleep=true
        predicates:
        - Path=/test/Hystrix
        filters:
        - name: Hystrix # Hystrix Filter的名称
          args: # Hystrix配置参数
            name: fallbackcmd #HystrixCommand的名字
            fallbackUri: forward:/fallback #fallback对应的uri
#Hystrix的fallbackcmd的时间
hystrix.command.fallbackcmd.execution.isolation.thread.timeoutInMilliseconds: 5000
```

* 添加 fallbackUri： [FallbackController](../demo/gateway/gateway-02/src/main/java/yore/filter/FallbackController.java)
* 添加Fallback后端服务： [GatewayTestController index()](../demo/feign/feign-hello/src/main/java/yore/controller/GatewayTestController.java)

* 启动项目：[HelloFeignApp](../demo/feign/feign-hello/src/main/java/yore/HelloFeignApp.java)
* 启动项目：[HystrixFilterApp](../demo/gateway/gateway-02/src/main/java/yore/filter/HystrixFilterApp.java)

* 访问： [http://localhost:8081/test/Hystrix](#)，因为这个接口会访问`http://localhost:8081/test/Hystrix?isSleep=true`，而这个请求休眠时间为10秒，
超过了fallback设置的5秒，会直接fallback到`http://localhost:8081/fallback`。


******************************
































