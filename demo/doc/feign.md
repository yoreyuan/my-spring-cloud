Spring Cloud Feign
====

Spring Cloud以微服务开发应用时，各个服务提供者都是以HTTP接口的形式对外提供，因此在服务消费者调用微服务提供者时，底层通过HTTP Client的方式访问。
这部分也可以使用JDK原生的URLConnection、Apache Http Client、Netty异步Http Client、Spring的RestTemplate来实现对微服务的调用。
但是最方便、最优雅的方式是通过Spring Cloud Open Feign进行服务间的调用。

Spring Cloud对Feign进行了增强，使Feign支持Spring MVC的注解，并整合了Ribbon等，让Feign的使用更加方便。


# 1. Overview
Feign是一个声明式的Web Server客户端，使用Feign只需要创建一个接口加上对应的注解(@FeignClient)，Feign支持编码器和解码器。
Feign是一种声明式、模块化的HTTP客户端，在Spring Cloud中使用Feign，可以做到使用HTTP请求访问远程服务，就像调用本地方法一样的，开发者完全感知不到是在调用远程方法，
更感知不到在访问HTTP请求。

Open Feign 地址： [https://github.com/OpenFeign/feign](https://github.com/OpenFeign/feign)  
Spring Cloud Open Feign 地址：[https://github.com/spring-cloud/spring-cloud-openfeign](https://github.com/spring-cloud/spring-cloud-openfeign)


## 小例子
在项目中添加Feign依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

在程序的主类上添加注解`@EnableFeignClients`，这个注解标识当程序启动时，会运行包扫描，扫描所有带`@FeignClient`的注解类并进行处理。

启动主应用程序`HelloFeignApp` ，打开浏览器，访问：http://localhost:8010/search/github?str=spring-cloud-dubbo

## Feign 工作原理
* 在开发微服务应用时，我们会在主程序入口添加`@EnableFeignClients`注解，来开启对Feign Client的扫描加载处理，根据Feign Client的开发规范，定义接口并加上`@FeignClient`注解。

* 当程序启动时，会进行包扫描，扫描`@FeignClient`的注解类，并将这些信息注入Spring IOC容器中。当定义的Feign接口中的方法被调用时，通过JDK的代理方式，
来生成具体的RequestTemplate。当生成代理时，Feign会为每个接口方法创建一个RequestTemplate对象，改对象封装了HTTP请求所需的全部信息，如请求参数名、请求方法等。

* 接下来由RequestTemplate生成Reques，然后把Request交给Client去处理(就是调用url服务的，可以是URLConnection、Apache的HTTP Client、Okhttp等)，
最后Client被封装到LocadBalanceClient类，这个类是结合Ribbon负载均衡发起的服务之间的调用。


# 2. Feign的基础功能
`@FeignClient`注解说明
* name: 指定FeignClient的名称，如果项目使用了Ribbon，name属性会作为微服务的名称，用于服务发现。
* url： 一般用于调试，可以手动指定@FeignClient调用的地址
* decode404： 当发生404错误时，如果该字段为true，会调用decoder进行解码，否则抛出FeignException。
* configuration： Feign配置类，可以自定Feign的Encoder、Decoder、LogLevel、Contract
* fallback： 定义容错处理类，当调用远程接口失败或超时时，会调用对应接口的容错逻辑，fallback指定的类必须实现@FeignClient标记的接口。
* fallbackFactory： 工厂类，用于生成fallback类实例，通过这个属性我们可以实现每个接口通用的容错逻辑，减少重复的代码
* path： 定义当前FeignClient的统一前缀
* primary： 是否将feign代理标记为主bean。 默认为true。

## 开启GZIP压缩
Spring Cloud Feign支持对请求和响应进行GZIP压缩，以提高通信效率。注意返回的类型也需要修改为`ResponseEntity<byte[]>`，否则会有乱码。

启动应用主程序，访问：http://localhost:8011/search2/github?str=spring-cloud-dubbo

## Feign支持属性文件属性
* 对单个指定特定名称的Feign进行配置
@FeignClient的配置信息可以通过application.properties或者application.yml来配置，
```
feign:
    client:
        config:
            feignName:      # 需要进行配置的feignName
                connectiTimeout: 5000        # 连接超时时间
                readTimeout： 5000           # 读超时时间设置
                loggerLevel： full           # 配置Feign的日志级别
                errorDecoder： xx.SimpleErrorDecoder     # Feign错误解码器
                retryer: xx.SimpleRetryer   # 配置重试
                requestInterceptors:        # 配置拦截器
                    - xx.example1.FooRequestInterceptor
                    - xx.example2.BarRequestInterceptor
                decode404: false
                encoder: xx.example.SimpleEncoder       # Feign的编码器
                decoder: xx.example.SimpleDecoder       # Feign的解码器
                contract： xx.example.SimpleContract    # Feign的Contract配置
```

## 作用于所有Feign的配置方式
@EnableFeignClients注解有`defaultConfiguration`属性，可以将默认配置写成一个类，比如这个配置类叫做DefaultFeignConfiguration.java，
在主程序的启动入口用`defaultConfiguration`来引用配置，
```
@EnableFeignClients(defaultConfiguration = DefaultFeignConfiguration.class)
```

与其等价的配置为(优先级比代码的高)：
```
feign:
    client:
        config:
            default:
                connectTimeout: 5000
                readTimeout: 5000
                loggerLever: base
```

## Feign Client开启日志
Feign为灭一个FeignClient都提供了一个feign.Logger实例，可以在配置中开启日志。

先在配置文件添加：
```
logging:
     level:
       xx.HelloFeignService: debug
```

通过Java代码的方式在主程序入口类中配置日志Bean，
```
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
```
也可以通过创建`@Configuration`注解类配置日志bean
```java
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloFeignServiceConfig {

    /**
     *
     * Logger.Level 的具体级别如下：
         NONE：不记录任何信息
         BASIC：仅记录请求方法、URL以及响应状态码和执行时间
         HEADERS：除了记录 BASIC级别的信息外，还会记录请求和响应的头信息
         FULL：记录所有请求与响应的明细，包括头信息、请求体、元数据
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
```

## Feign超时设置
Feign的调用分两层，Ribbon的调用和Hystrix的调用，高版本的Hystrix默认是关闭的。

* 设置Ribbon
```
# 请求处理的超时时间
ribbon.ReadTimeout: 120000
# 请求连接的超时时间
ribbon.ConnectTimeout: 30000
```

* 如果卡其Hystrix，配置超时处理
```
feign.hystrix.enabled: true
hystrix:
    shareSecurityContext: true
    command: 
        default:
            circuitBreaker:
                sleepWindowInMilliseconds: 100000
                forceClosed: true
            execution:
                isolation:
                    thread: 
                        timeoutInMilliseconds: 600000
```

# 3. Feign 实战运用
## Feign默认Client的替换
Feign在默认情况下使用的是JDK原生的URLConnection发送HTTP请求，没有连接池，但是对于每个地址会保持一个长链接。

我们可以使用Apache Http Client替换Feign原始的HTTP Client，通过设置连接池、超时时间等对服务之间的调用调优。Spring Cloud从Brixtions.SR5版本开始支持这种替换，

**1** 使用HTTP Client替换Feign默认Client
引入依赖：
```
    <!-- 使用Apache HttpClient替换Feign原生httpclient -->
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient</artifactId>
    </dependency>
    <dependency>
        <groupId>com.netflix.feign</groupId>
        <artifactId>feign-httpclient</artifactId>
        <version>8.18.0</version>
    </dependency>
```

修改`application.yml`配置文件
```
server:
  port: 8010
spring:
  application:
    name: feign-httpclient

feign:
  httpclient:
    enabled: true
```

访问：http://localhost:8010/search/github?str=spring-cloud-openfeign

**2** 使用okhttp替换Feign默认Client
使用okhttp主要因为它有如下特性：
* 支持[SPDY](https://baike.baidu.com/item/SPDY/3399551?fr=aladdin)，可以合并多个到同一个主机的请求
* 使用连接池技术减少请求的延迟（如果SPDY是可用的话）
* 使用GZIP压缩减少传输的数据量
* 缓存响应避免重复的网络请求

引入依赖：
```xml
    <dependency>
        <groupId>io.github.openfeign</groupId>
        <artifactId>feign-okhttp</artifactId>
    </dependency>
```

修改`application.yml`配置文件
```
server:
  port: 8011
spring:
  application:
    name: feign-okhttp

feign:
  httpclient:
    enabled: false
  okhttp:
    enabled: true
```

访问：http://localhost:8011/search/github?str=spring-cloud-openfeign

## Feign的Post和Get多参数传递
在服务和服务之间进行传递时，难免会有多参数传递。

在Web开发中Spring MVC是支持GET方式直接保定POJO的，但是Feign的实现并为覆盖所有Spring MVC的功能，目前的实现方式有很多，
这里采用一种最佳的实践方式，通过Feign拦截器的方式处理。

依次启动: eureka-server、feign-provider、feign-consumer。
访问: http://localhost:8011/swagger-ui.html，可以看到Swagger的访问测试页面。

[关于 Swagger2](#关于-Swagger2)

* 测试Feign的Git多参数传递
在Swagger2页面的`/user/add`栏，传入请求参数(参数示例可以参考页面)，然后点击下面的<kbd>Try it out!<kbd>
```json
{
  "age": 27,
  "id": 1,
  "name": "yore"
}
```

可以在页面看到请求返回的body,和状态码，响应头等信息

* 测试Feign的Post多参提交
同样在Swagger2页面的`/user/update`栏，传入请求参数(参数示例可以参考页面)，然后点击下面的<kbd>Try it out!<kbd>
```json
{
  "age": 22,
  "id": 2,
  "name": "anonymity"
}
```

## Feign的文件上传
关于项目详细说明，可查看[feign-upload](../../example/feign-upload/readme.md)








******

# 关于 Swagger2
官方文档可查看：[Springfox Reference Documentation]()http://springfox.github.io/springfox/docs/current/)

## Manve 
Release版，pom添加如下配置
```
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.5.0</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.5.0</version>
        </dependency>
```

## 代码
* 配置类上添加： `@EnableSwagger2`和 `@Configuration` 注解

注入一个Docket的Bean

在接口类，controller层的类、方法、参数添加响应注解


# 访问 
http://localhost:8011/swagger-ui.html

点击页面的需要查看的接口类右侧的 `Show/Hide`可接口信息
![swagger-ui-01.png](img/swagger-ui-01.png)



