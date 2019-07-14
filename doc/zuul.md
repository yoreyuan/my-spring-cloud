Spring Cloud Zuul
---
Zuul是由NetFlix孵化的一个致力于"网关"解决方案的开源组件，在动态路由、监控、弹性、服务治理以及安全方面起着举足轻重的作用。

*********
基础篇
===

# 1 概述
Zuul简介：Zuul是从设备和网站到后端应用程序所有请求的前门，为内部服务提供可配置的对外URL到服务的映射关系，基于JVM的后端路由器。

器具备以下功能：
* 认证与鉴权
* 压力控制
* 金丝雀测试
* 动态路由
* 负载削减
* 静态响应处理
* 主动流量管理

其底层基于Servlet，本质组件是一系列Filter所构成的责任链。Spring Cloud Finchley版本继续沿用NetFlix Zuul 1.x版本，而不是2.x版本，
主要是因为由于Zuul 2.x版本改动相较于1.x较大，考虑到整个生态的稳定性，以及使用者升级版本会遇到的种种问题，虽然2.x底层使用Netty性能更好，
Finchley版还是继续使用1.x版本。另外，由于Spring Cloud Gateway已经孵化成功，相较于Zuul在功能以及性能上都有明显提上。

# 2 小例子
模块名 | 端口号 | 说明
eureka-server | 8761 |
zuul-server1 | 5555 | 
client-a    | 7070 | Zuul的一个下游服务

### 2.1 zuul-server
* 引入依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
```

* 修改配置，在配置文件添加
```yaml
zuul:
  routes:
    zuul-gateway:
      path: /client/**
      serviceId: client-a
```

* 启动类上添加注解：`@EnableZuulProxy`

### 2.2 创建一个下游服务 
可以直接沿用Ribbon中的 [client-a](../demo/ribbon/client-a) 模块代码


### 2.3 测试
* 启动 eureka-server 注册中心
* 启动 zuul-server1 网关服务
* 启动 client-a 下游服务
* 使用postman发送一次请求(直接请求服务)：http://localhost:7070/add?a=100&b=400
* 使用postman发送一次请求（通过zuul请求服务）：http://localhost:5555/client/add?a=100&b=400


## 3 Zuul典型配置
### 3.1 路由配置
* 单实例serviceId映射。简化配置，可以将注释部分的配置简化为如下配置
```yaml
#zuul:
#  routes:
#    client-a:
#      path: /client/**
#      serviceId: client-a

zuul:
  routes:
    client-a: /client/**
```

* 单实例url映射。如下，使用url替代serviceId路由
```yaml
zuul:
  routes:
    client-a:
      path: /client/**
      url: http://localhost:7070 #client-a的地址
```

* 多实例url映射。如下，脱离eureka让zuul结合ribbon实现路由负载均衡
```yaml
zuul:
  routes:
    ribbon-route:
      path: /ribbon/**
      serviceId: ribbon-route

ribbon:
  eureka:
    enabled: false  #禁止Ribbon使用Eureka

ribbon-route:
  ribbon:
    NIWSServerListClassName: com.netflix.loadbalancer.ConfigurationBasedServerList
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule     #Ribbon LB Strategy
    listOfServers: localhost:7070,localhost:7071     #client services for Ribbon LB

```

* forward本地跳转
```yaml
zuul:
  routes:
    client-a:
      path: /client/**
      url: forward:/client
```

* 相同路径的加载规则
如果同一个映射路径对应多个服务，按照加载顺序，最末加载的映射会把之前的映射规则覆盖掉。


### 路由统配符
规则 | 释义 | 示例 
:---- | :---- | :----
/** | 匹配任意数量的路径与字符 | /client/add, /client/mnl, /client/a, /client/add/a, /client/mul/a/b
/*  | 匹配任意数量的字符      | /client/add/, /client/mul, /client/a
/?  | 匹配单个字符           | /client/a, /client/b


## 3.2 功能配置
### 3.2.1 路由前缀
如下，这样方位任何接口是，路径前都会添加 `/pre`
```yaml
zuul:
  prefix: /pre                  #前缀
  routes:
    client-a: /client/**
```

### 3.2.2 关闭Zuul的代理从请求路径移除的功能，
关闭之后，请求路径是 `/pre/client/add`，实际起作用的还是 `/pre/client/add`，一般不选用这个配置。
```yaml
zuul:
  prefix: /pre
  routes:
    client-a:
      path: /client/**
      serviceId: client-a
      stripPrefix: false
```

### 2.3.3 服务屏蔽与路径屏蔽
有时为了避免某些服务或者路径的侵入，可以将他们屏蔽掉。加上 `ignored-services`与 `ignored-patterns`之后，
Zuul在拉取服务列表时，创建映射规则时，会忽略掉 client-b、/**/div/**接口
```yaml
zuul:
  ignored-services: client-b    #忽略的服务，防服务侵入
  ignored-patterns: /**/div/**  #忽略的接口，屏蔽接口
  prefix: /pre                  #前缀
  routes:
    client-a: /client/**
```

### 2.3.4 敏感投信息
Zuul通过配置敏感投信息，切断它和上下层服务之间的交互。
```yaml
###  敏感头设置 
zuul:
  prefix: /pre
  routes:
    client-a:
      path: /client/**
      sensitiveHeaders: Cookie,Set-Cookie,Authorization
      serviceId: client-a
      stripPrefix: false
```

### 2.3.5 重定向问题
```yaml
### 服务忽略、路径忽略、前缀、重定向问题 

zuul:
  add-host-header: true         #重定向header问题
  routes:
    client-a: /client/**
```

### 2.3.6 重试机制
在生产环境，由于各种各样的原因，可能会使一次请求偶然失败，
```yaml
### 重试机制
zuul: 
  retryable: true #开启重试
ribbon:
  MaxAutoRetries: 1 #同一个服务重试的次数(除去首次)
  MaxAutoRetriesNextServer: 1  #切换相同服务数量
```

<br/>

*******************

<br/>


中级篇
===
至此我们能够基本搭建起一套可防止服务侵入的"网关-服务"微服务架构，但实际企业级开发中是远远不够的，在实际项目中，我们往往会在网关层设计鉴权、限流、动态路由、文件上传、参数转换，
以及做其他逻辑与业务处理。

# 1 Spring Cloud Zuul Filter链
## 1.1 工作原理
Zuul的核心逻辑是由一系列紧密配合工作的Filger来实现的，他们能够在HTTP请求或者响应的时候执行相关操作。Zuul Filter的主要特性有以下几点：
* **Filter的类型**：Filter类型决定了此Filter在Filter链中的执行顺序。可能是路由动作发生前、可能是路由动作发生时、可能是路由动作发生后、也可能是路由动作发生异常时。
* **Filter的执行顺序**：同一种类型的Filter可以通过FilgerOrder方法设定执行顺序。一般会根据业务的执行顺序需求，来设定自定义Filger的执行顺序。
* **Filter的执行先决条件**：Filger运行需要的标准或条件
* **Filter的执行效果**：符合某个Filter执行条件，产生的执行效果。

Zuul中一共有四种不同声明周期的Filter：
* `pre`： 在Zuul按照规则路由到下级服务之前执行。如果需要对请求进行预处理，比如鉴权、限流等，都应考虑在此类Filter实现。
* `route`：这类Filter是Zuul路由动作的执行者，是Apache HTTPClient 或Netflix Ribbon构建和发送原始HTTP请求的地方，目前一直吃OkHttp。
* `post`：这类Filter是在原服务返回结果或者异常信息发生后执行的，如果需要返回信息做一些处理，则在此类Filter进行处理。
* `error`：在整个生命周期内如果发生异常，则会进入error Filger，可做全局异常处理。

在实际项目中，往往需要自实现以上类型的Filter来对请求链路进行处理，根据业务的需求，选取响应生命周期的Filter来打成目的。

## 1.2 Zuul原生Filter
Zuul使用`@EnableZuulProxy`并搭配`spring-boot-starter-actuator`，注意配置
```
management.endpoints.web.exposure.include=*
#management.endpoint.shutdown.enabled=true
management.endpoint.health.show-details=always
```
然后可以查看如下接口
* http://localhost:5555/actuator/routes
* http://localhost:5555/actuator/routes/details
* http://localhost:5555/actuator/routes/filters

**禁用某个原生过滤器：zuul.\<SimpleClassName>.\<filterType>.disable=true**；例如禁用SendErrorFilter，配置文件中可添加
`zuul.SendErrorFilter.error.disable=true`。

## 1.3 多级业务处理
### 1.3.1 实现自定义Filter
在Zuul中实现自定义Filter，集成ZuulFilter类即可，ZuulFilter是一个抽象类，我们需要实现它的几个方法：
* `String filterType() `： 使用返回值设定Filter类型，可以设置为： pre、route、post、error类型
* `int filterOrder() `：使用返回值设定Filter执行次序
* `boolean shouldFilter() `： 使用返回值设定该Filter是否执行，可以作为开关来使用
* `Object run() `：Filter里面的核心执行逻辑，业务处理在此处编写。

## 1.4 使用Groovy编写Filter
Groovy语言是基于JVM的一门动态语言，它结合了Python、Ruby和Smaltalk的许多强大特性，能无缝引入Java代码与Java库，常常被用作Java的扩展语言来使用。
Zuul中提供了Groovy的编译类 `com.netflix.zuul.groovy.GroovyCompiler`，结合这个类，可以使用Groovy来编写自定义的Filter。

* 为工程添加Groovy依赖：
```xml
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-all</artifactId>
    <version>2.4.17</version>
</dependency>
```

* 创建一个GroovyFilter的`Groovy Class`，并继承`com.netflix.zuul.ZuulFilter.ZuulFilter`，实现其中的方法即可
[GroovyFilter.groovy](../demo/zuul/zuul-server2/src/main/java/yore/filter/groovy/GroovyFilter.groovy)

* 注册GroovyFilter.groovy
在启动类中添加如下代码：
```java
    @Component
    public static class GroovyRunner implements CommandLineRunner {
        @Override
        public void run(String... args) throws Exception {
            MonitoringHelper.initMocks();
            FilterLoader.getInstance().setCompiler(new GroovyCompiler());
            try {
                FilterFileManager.setFilenameFilter(new GroovyFileFilter());
                // 使用绝对路径，每隔20秒扫描一次
                FilterFileManager.init(20, "/home/spring/filter/groovy");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
```

* 一次启动项目：eureka-server、zuul-server2、client-a，访问：http://localhost:5555/client/mul?a=100&b=300
在控制台可以看到打印的Header的信息，可以对`GroovyFilter.groovy`修改，大概20秒后，可以看到执行信息。

* 这里我这里出现了一个问题，当引入``后，zuul暴露的接口或者调用接口时开始需要认证，此时需要配置文件中显示定义用户和密码
```
spring.security.user.name=admin
spring.security.user.password: admin
```
然后访问接口时加上用户认证：http://admin:admin@localhost/client/mul?a=100&b=300


# 2 Spring Cloud Zuul权限集成
## 2.1 应用权限概述

### 2.1.1 自定义权限认证Filter
由于Zuul在请求转发全程的可控性，我们可以在RequesContext的基础上做任何事情。只需要在上一部分的基础上对Filter执行顺序上做调整，就可以专门用于对特定内容做权限认证。

这种方式的优点是实现灵活度搞，可整合有权限系统，对原始系统微服务化特别友好，缺点是需要开发一套新的逻辑，维护成本增加，而且也会使得调用链路变得紊乱。

### 2.1.2 OAuth2.0 + JWT
OAuth2.0 是业界关于"授权-认证"比较成熟的面向资源的授权协议。在整个流程中，用户是资源拥有者，其关键在于客户端需要资源拥有这的授权。

JWT（JSON Web Token）是一种使用JSON格式来规约Token或者Session的协议。

## 2.2 Zuul + OAuth2.0 + JWT实战
在服务注册到注册中心之后，由于Zuul来移到各个请求到内部敏感服务的，因此，在Zuul网关层实现与下游服务之间的鉴权至关重要。

### 2.2.1 auth-server
项目 [auth-server](../demo/zuul/auth-server)


### 2.2.2 zuul-server
项目 [zuul-server](../demo/zuul/zuul-server2)

### 2.2.3 client
主要提供下游服务接口，添加了一个按照规则解析jwt token的接口。项目 [client-a2](../demo/zuul/client-a2)

### 2.2.4 测试
依次启动：eureka-server、zuul-server2、client-a2、auth-server。  
使用浏览器访问： http://localhost:5555/client/test
此时会出现如下页面，原因是此时还未授权，这是直接调用接口是不通的
![zuul-auth-01.png](img/zuul-auth-01.png)

使用浏览器访问：http://localhost:5555，会转到：http://localhost:7777/uaa/login 进行登录，使用用户名和密码admin登录。

**这里通过谷歌浏览器，一直授权失败，后面改用postman，是可以请求成功的，如果不行可以更换浏览器试下，后面试了下Safari浏览器没有问题**    
输入：http://localhost:5555/client/test  
点击 <kbd>Authorization</kbd>   
Type选：OAuth 2.0   
点击 <kbd>Get New Access Token</kbd>  

此时会弹出一个框，如下填写，点击`Request Token`：
![postman-oauth-0.png](img/postman-oauth-0.png)


在弹出的`Login Page`，填入User和Password，都为我们设置的admin。回到postman，点击`User Token`。再次请求接口。
![postman-oauth-success.png](img/postman-oauth-success.png)


# 3 Spring Cloud Zuul限流
构建一个自我修复的系统一直是各大企业进行架构设计的难点所在，在Hystrix中，我们可以通过熔断器来实现。其实，除了对异常流量就行降级处理之外，
我们可以做一些其他操作保护我们的系统免受"雪崩之灾"，比如：流量排队、限流、分流等。这一部分我们基于Zuul来谈谈限流策略的应用与实现。

## 3.1 限流算法
### 3.1.1 漏桶 （Leaky Bucket）
漏桶的原型是一个底部有镂空的桶，桶上方有一个入水口，水不断地流入桶内，桶下方的漏孔就会以一个相对恒定的速率漏水。在入大于出的情况下，桶在一段时间之后就会被填满，
这时候多余的水就会溢出；而在入小于出的情况下，漏桶则不起任何作用。

后来人们将这个经典模型运用在流量整形上面，通过漏桶算法的约束，突发流量可以被整形为一个规整的流量。当我们的请求或者具有一定体量的数据流涌来的时候，
在漏桶的作用下，流量被整形，不能满足要求的部分被削减掉。这部分溢出的流量并非完全丢弃，我们可以把它收集到一个队列里面，做流量排队。

### 3.1.2 令牌桶 
桶里面存放着令牌，而令牌又是以一个恒定的速率被加入桶中，可以积压，可以溢出。当我们的数据流涌来的时候，量化请求用于获取令牌，如果取到令牌则放行，
同时桶内丢掉这个令牌；如果不能取到令牌，请求被丢弃。

由于令牌桶内可以存在一定数量的令牌，那么就可能存在一定程度的流量突发，这个也是区别漏桶算法与令牌桶算法不同使用场景的。

## 3.2 流量实战
Zuul中实现流量最简单的方式是使用定义Filter加上相关限流算法，其中可能会考虑多借点部署，一位算法那的原因，这时候需要一个K/V存储工具（推荐使用Redis）。
当然如果Zuul是单节点应用，限流方式的选择就会广很多，完全可以将相关prefix放在内存中，方便又快捷。

这里介绍一个开箱即用的工具 [`spring-cloud-zuul-ratelimit`](https://github.com/marcosbarbero/spring-cloud-zuul-ratelimit)，
这个是一位国外友人专人针对Zuul编写的限流库，提供多种细粒度策略：
* user：认证用户名或匿名，针对某个用户粒度进行限流。
* origin：客户机ip，针对请求客户及ip粒度进行限流。
* url： 特定url，针对某个请求url粒度进行限流。
* serviceId：特定服务，针对某个服务id粒度进行限流。

以及多种粒度临时变量存储方式：
* IN_MEMORY: 基于本地内存，底层是ConcurrentHashMap。
* REDIS: Redis的K/V存储。
* CONSUL: Consul的K/V存储。
* JPA: Spring Data JPA，基于数据库。
* BUKET4J：一个使用Java编写的基于令牌桶算法的限流库，其有四种模式，JCache、Hazelcast、Apache Ignite、Inifinispan，其中后面三种支持异步。

* 1 zuul-server 编写
[zuul-server1](../demo/zuul/zuul-server1)

引入依赖：
```xml
<dependency>
    <groupId>com.marcosbarbero.cloud</groupId>
    <artifactId>spring-cloud-zuul-ratelimit</artifactId>
    <version>2.2.4.RELEASE</version>
</dependency>
```

* 2 测试
依次启动：eureka-server、zuul-server1、client-a    
正常调用接口： http://localhost:5555/client/add?a=100&b=200

当连续请求时会报如下的错误：
```json
{
    "timestamp": "2019-07-13T11:22:52.533+0000",
    "status": 429,
    "error": "Too Many Requests",
    "message": "429"
}
```

针对`myServiceId`的路由限流：
配置文件中添加：
```yaml
zuul:
  ratelimit:
    policy-list:
      client-a:          #特定服务的路由  client-a时
        - limit: 10               #可选 - 每个刷新间隔窗口的请求数限制
          quota: 1000             #可选 - 每个刷新间隔窗口的请求时间限制（以秒为单位）
          refresh-interval: 60    #默认值 (in seconds)
          type:                   #optional
            - user
            - origin
            - url
```
再次连续请求，此时可以正常通过。


# 4 Spring Cloud Zuul动态路由
## 4.1 动态路由概述
前面部分涉及到的基本都是"静态路由"，但在正常产品迭代过程中，一个复杂的系统难免经历新的上线过程，这个时候我们就不能轻易停掉线上的某些映射链路，那么问题来了，
Zuul是在启动的时候将配置文件中的映射规则写入内存，要新建映射规则，只能修改了配置文件之后再重新启动Zuul应用。能不能有一种方法，既能按需修改映射规则，又能是服务免于重启之痛呢？
答案是有的，目前有两种解决方案实现"动态路由"
* 结合Spring Cloud Config + Bus，动态刷新配置文件。这种方式的好处是不用Zuul维护映射规则，可以随时修改，随时生效；唯一不好的地方是需要单独集成一些使用并不频繁的组件，Config没有可视化界面，维护起规则来也相对麻烦。
* 重写Zuul的配置读取方式，采用事件刷新机制，从数据库读取路由映射规则。此种方式因为基于数据库，可以轻松实现管理界面，灵活度较高。

Spring Cloud生态推荐采用使用第一种方式，这个会在后面介绍。这里主要围绕第二种方式展开。

## 4.2 实现原理剖析
### 1 DiscoveryClientRoutLocator
`org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator`这个类是Zuul中对于路由配置信息读取与新节点注册变更的操作类。

* `locateRoutes()`方法继承自`SimpleRouteLocator`类并重写了规则，该方法的主要功能就是将配置文件中的映射规则信息包装成`LinkedHashMap<String, ZuulRoute> `，键String是路径path，
值ZuulRoute是配置文件的封装类，以往所见的配置映射读取进来就是使用ZuulRoute来封装。

* `refresh()`实现自`RefreshableRouteLocator`接口，添加刷新功能必须要实现此方法。`doRefresh()`方法来自`SimpleRouteLocator`类。

### 2 SimpleRouteLocator
它是`DiscoveryClientRoutLocator`父类，此类基本实现了`RouteLocator`接口，对读取的配置文件信息做一些基本处理，提供方法`doRefresh()`与`locateRoutes()`供子类实现刷新策略与映射规则加载策略，
这两个方法都是`protected`修饰，是为了让子类不用维护此类一些成员变量就能够实现刷新或者读取路由的功能。

### 3 ZuulServerAutoConfiguration
在低版本的Spring Cloud Zuul，这个类叫做ZuulConfiguration，位于`org.springframework.cloud.netflix.zuul`包中，主要目的是注册各种过滤器、监听器以及其他功能。
Zuul在注册中心新增服务后刷新监听器也是在此注册的，底层采用`Spring的ApplicationListener`来实现的。由方法`onApplicationEvent(ApplicationEvent event)`可知，
Zuul会接收4中事件：`ContextRefreshedEvent、RefreshScopeRefreshedEvent、RoutesRefreshedEvent、RoutesRefreshedEvent`。

### 4 ZuulHandlerMapping
这个类在`org.springframework.cloud.netflix.zuul.web`包下，主要是将本地配置的映射关系映射到远程的过程控制器。其中的`dirty`属性，主要用来控制当前是否需要重新加载映射的配置信息的标记，
在Zuul中每次进行路由操作的时候都会检查这个值，如果为true，就会触发配置信息的重新加载，同时将其回设为false，同时由`setDirty(boolean dirty)`方法可知，启动刷新动作必须实现`RefreshableRouteLocator`接口。

## 4.3 基于DB的动态路由实战
这里的DB选用Mysql，MongoDB也是一个不错的选择。

[zuul-server3](../demo/zuul/zuul-server3)

测试：   
一次启动：eureka-server、zuul-server3、client-a，  
分别请求
- http://localhost:5555/client/mul?a=100&b=200      
- http://localhost:5555/client-a/mul?a=100&b=200
- http://localhost:5555/baidu


# 5 Spring Cloud Zuul 灰度发布
灰度发布是指在系统迭代新功能时的一种平滑过渡的线上发布方式。灰度发布是指在原有的系统的基础上，额外增加一个新版本，这个新版本包含我们需要待验证的新功能，
随后用负载聚恒器引入一小部分流量到这个新版本应用，如果整个过程没有出现任何差错，再平滑地把线上系统或服务一步步替换成新版本。

对于Spring Cloud 微服务生态来说，粒度一般是一个服务，往往通过使用某些带有特定标记的流量来充当灰度发布过程中的'小白鼠'，并且目前有比较好的开源项目来做这个事情。

## 5.1 实战之一
本节使用的是基于Eureka元数据的一种方式，在Eureka里面，一共有两种元数据：
1. 标准元数据：这种元数据时服务的各种注册信息，比如ip、端口、服务健康信息、续约信息等，存储于专门为服务开辟的注册表中，用于其他组件取用以实现微服务生态。
2. 自定义元数据：自定义元数据是使用`eureka.instance.metadata-map.<key>=<value>`来配置的，内部其实就是维护了一个Map来保存自定义元数据信息，可以配置在远程服务，随服务一并注册保存在Eureka注册表中，
对微服务生态的任何行为都没有影响，除非我们知道其特定的含义。

### 1 zuul-server、client-a编写
首先需要在zuule-server引入一个开源项目包 [`ribbon-discovery-filter-spring-cloud-starter`](https://github.com/jmnarloch/ribbon-discovery-filter-spring-cloud-starter)，

模块 | 端口
---- | ---- 
[client-a](../demo/ribbon/client-a) | 7070、7071-正常运行的线上业务，7072-需要上线的灰度服务
[zuul-server3](../demo/zuul/zuul-server3) | 5555

### 2 测试
* 启动 eureka-server
* 启动 zuul-server
* 启动 client-a：分别指定`spring.profile`为node1、node2、node3启动三个服务实例

* 使用postman直接请求: http://localhost:5555/client/add?a=100&b=300，此时连续点击会发现轮询调用7070和7071端口（如果是用上个项目是请修改mysql配置的client映射）
* 在header中添加 `gray_mark=enable` 再次请求，发现此类的路由全部会调用7072端口。


# 6 Spring Cloud Zuul 文件上传
## 6.1 文件上传实战
文件上传的场景，基本很多开发都需，Zuul作为网关中间件，自然也会面临文件上传的考验。Zuul的文件上传功能是从Spring Boot承袭过来的，所以也需要Spring Boot的相关配置。

### zuul-server
配置文件中需要对上传的文件的一些阈值做限定，以及对熔断或者负载均衡超时时间做配置

[zuul-server1](../demo/zuul/zuul-server1)

### 测试
* 启动 eureka-server
* 启动 zuul-server1
* postman中 POST 请求：http://localhost:5555/upload，并在body中选择`form-data`，key类型选择为file，选择文件

上传成功会返回文件的路径。如果发生`Caused by: java.nio.file.AccessDeniedException: /undertow124295582067686520upload`请确定涉及的路径是否有响应的权限。

## 6.2 文件上传乱码解决
当上传的文件名发生中文乱码时，可以尝试调用 `http://localhost:5555/zuul/upload`接口


# 7 Spring Cloud Zuul 使用小技巧
## 7.1 饥饿加载
Zuul内部默认使用Ribbon来调用远程服务，在第一次经过Zuul调用时，需要去注册中心读取服务注册表，初始化Ribbon负载信息，这是一种懒加载策略，当服务过多时，这个过程是极其耗时的。
为了避免这个问题，可以在启动Zuul是就进行饿加载应用程序的上下文信息，配置文件添加
```yaml
zuul:
  ribbon:
    eager-load:
      enabled: true #开启饥饿加载
```

## 7.2 请求体修改
在客户端对Zuul发送post请求之后，由于某些原因，在请求扫下游服务之前，需要对请求体进行修改，常见的是对form-data参数的增减，对application/json的修改，对请求体做UpperCase等。
Zuul中可以通过新增一个PRE类型的Filter对请求体进行修改来解决。

```
    @Override
	public Object run() throws ZuulException {
		try {
            com.netflix.zuul.context.RequestContext context = com.netflix.zuul.context.RequestContext.getCurrentContext();
            String charset = context.getRequest().getCharacterEncoding();
            InputStream in = (InputStream)context.get("requestEntity");
            if(in == null){
                in = context.getRequest().getInputStream();
                String body = StreamUtils.copyToString(in, Charset.forName(charset));
                // 新增参数
                body += "&weight=140";
                byte[] bytes = body.getBytes(charset);
                context.setRequest(new com.netflix.zuul.http.HttpServletRequestWrapper(context.getRequest()) {

                    @Override
                    public javax.servlet.ServletInputStream getInputStream() throws IOException {
                        return new com.netflix.zuul.http.ServletInputStreamWrapper(bytes);
                    }

                    @Override
                    public int getContentLength() {
                        return bytes.length;
                    }

                    @Override
                    public long getContentLengthLong() {
                        return bytes.length;
                    }
                });
            }

        }catch (IOException e){
            ReflectionUtils.rethrowRuntimeException(e);
        }
		return null;
	}
```


## 7.3 使用okhttp替换HttpClient
Zuul中使用Okhttp替换HttpClient，首先需要在pom中增加okhttp依赖
```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
</dependency>
```

然后配置文件禁用HttpClient并启用okhttp
```yaml
#禁用HttpClient并开启OkHttp
ribbon:
  httpclient:
    enabled: false
  okhttp:
    enabled: true
```

## 7.4 重试机制
Zuul作为网关中间件，在出现偶然请求失败时进行适当的重试是十分必要的，重试可以有效避免一些突发原因引起的请求丢失。Zuul中的重试机制是配合Spring Retry与Ribbon来使用的。
pom中引入依赖：
```xml
<dependency>  
    <groupId>org.springframework.retry</groupId>  
    <artifactId>spring-retry</artifactId>  
</dependency>  
```

对重试进行相应的配置，也可以对单个映射规则进行重试`zuul.routes.<route>.retryable=true`
```yaml
spring:
  cloud:
    loadbalancer:
      retry:
        enabled: true #内部默认已开启，这里列出来说明这个参数比较重要

zuul:
  retryable: true #开启重试，D版之后默认为false，需要手动开启

#重试机制配置
ribbon:
  ConnectTimeout: 3000
  ReadTimeout: 60000
  MaxAutoRetries: 1 #对第一次请求的服务的重试次数
  MaxAutoRetriesNextServer: 1 #要重试的下一个服务的最大数量（不包括第一个服务）
  OkToRetryOnAllOperations: true
```

## 7.5 Header传递
问题：在Zuul中对请求做了一些处理，需要把处理的结果发送到下游服务，但又不能影响请求体的原始特征。
```
    @Override
	public Object run() throws ZuulException {
		RequestContext context = RequestContext.getCurrentContext();
		context.addZuulRequestHeader("result", "to next service");
		return null;
	}
```
这样就可以动态增加一个header来传递给下游服务使用了，

## 7.6 整合Swagger2 调试源服务
Swagger 是一个可视化的API测试工具，可以和应用完美融合，通过声明接口注解的方式，方便快捷地获取API调试界面，是的前后端可以很好地对接，极大地减少了后端服务人员编写接口文档的时间。

[zuul-server1](../demo/zuul/zuul-server1)
[client-a](../demo/ribbon/client-a)

* 启动 zuul-server1
* 启动 client-a
* 访问（等client-a启动后，刷新页面）：http:localhost:5555/swagger-ui.html



<br/>

*******************

<br/>


高级篇
===

# 1 Spring Cloud Zuul多层负载

## 1.1 痛点场景
在Spring Cloud微服务架构体系中，所有请求的前门的网关Zuul承担着请求转发的主要功能，对后端服务起着举足轻重的作用。当业务体量猛增时，得益于Spring Cloud的横向拓展能力，
往往加节点、加机器就可以是的系统支撑性能获得大大提升，但仅仅增加服务而不增加网关还是会有性能瓶颈的。为了扩展Zuul，可以启动多个服务实例，上层再通过Nginx进行负载。
但是这样如果某个Zuul服务挂了，Nginx转发到这个实例的请求就会全部失败，性能还是会突然下降

## 1.2 解决方案
OpenResty整合了Nginx与Lua，实现了可伸缩的Web平台，内部集成了大量精良的Lua库、第三方模块以及多数的依赖项。能够非常快捷地搭建处理高并发、扩展性极高的动态Web应用、Web服务和动态网关。

我们可以使用Lua脚本模块与注册中心构建一个服务动态增减的机制，通过Lua获取注册中心状态为UP的服务，动态地加入到Nginx的均衡列表中去，由于这种架构模式实际了不知一个负载均衡器，我们称其为**多层负载**。

Spring Cloud中国社区针对这一场景开源先关的Lua插件源码，GitHub地址：[https://github.com/SpringCloud/nginx-zuul-dynamic-lb](https://github.com/SpringCloud/nginx-zuul-dynamic-lb)


# 2 Spring Cloud Zuul应用优化
## 2.1 概述
Zuul是简历在Servlet上的同步阻塞架构，所以在处理逻辑上面是合线程密不可分的，每一次请求多需要从线程池获取一个线程来维持I/O操作，路由转发的时候又需要从HTTP客户端获取线程来维持连接，这就会导致一个组件占用两个线程资源。
所以，在使用Zuul时，对这部分的优化很有必要，一个好的优化体系会使得应用支撑的业务体量更大，也能最大化利用服务器资源。

Zuul的优化分为以下几类性：
* 容器优化： 内置容器Tomcat与Undertow的比较与参数设置。
* 组件优化： 内部集成的组建优化，如Hystrix线程隔离、Ribbon、HttpClient与OkHttp选择。
* JVM参数优化： 适用于网关应用的JVM参数建议
* 内部优化： 一些内部原生参数，或者内部源码，以一种更恰当的方式重写它们。

## 2.2 容器优化
一般是将默认的内嵌容器Tomcat替换成Undertow。Undertow体统阻塞或基于XNIO的非阻塞机制，它的包不足1MB，内嵌模式运行时的堆内存占用只有4MB左右。

pom中进行这样替换
```xml
<dependencys>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-tomcat</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
   
    <!--不用Tomcat,使用undertow -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-undertow</artifactId>
    </dependency>
    <!--<dependency>
        <groupId>io.undertow</groupId>
        <artifactId>undertow-servlet</artifactId>
    </dependency>-->
</dependencys>
```

修改`undertow`配置参数：

配置项 | 默认值 | 说明
---- | ---- | ----
server.undertow.io-threads | Math.max(Runtime.getRuntime().avaliablePrecessors(), 2) | 这支IO线程数，它主要执行非阻塞的任务，他们会负责多个连接，默认设置每个CPU核心的一个线程。不要设置过大，如果过大，项目启动会报错：打开文件数过多。
server.undertow.worker-threads | ioThreads*8 | 阻塞任务线程数，当执行类似Servlet请求阻塞IO操作，undertow会从这个线程池中取到线程。它的值设置取决于系统线程执行任务的阻塞系数，默认值是IO线程数*8
server.undertow.direct-buffers | 取决于JVM最大可用内存大小（longmaxMemory=Runtion.getRuntime().maxMemory();)，小于64MB默认为false，其余为true | 是否分配直接内存（NIO直接分配的堆外内存）
server.undertow.buffer-size | 最大可用内存<64MB: 512字节；64MB<=最大可用内存<128MB: 1024字节；最大可用内存>128MB: 1024*12-20字节 | 每块buffer的空间大小，空间越小利用月充分，不要设置太大，以免影响其他应用，合适即可
server.undertow.buffers-per-region | 最大可用内存<64MB: 512字节；10；64MB<=最大可用内存<128MB: 10；最大可用内存>128MB: 20 | 每个区域分配的buffer数量，所有pool的大小是buffer-size * buffers-per-region

## 2.3 组件优化
### 2.3.1  Hystrix
* 适当调大超时时间或者禁用Hystrix的超时时间。
* 选择合适的Hystrix线程隔离方式： THREAD(官方推荐)、SEMAPHORE

### 2.3.2 Ribbon
* 配置超时时间
* 开启重试机制

## 2.4 JVM参数优化
凡是Java应用，都免不了JVM优化的过程。网关最需要的是吞吐量，所以优化应一次为切入点综合考虑。这里推荐使用Parallel Scavenne收集器，注意他有一个参数`-XX:UseAdaptiveSizePolicy`，
建议将它关闭，改为 `-XX:UseAdaptiveSizePolicy`，再根据实际情况调整Eden区域Survivor区的比例。这里还有一个参数比较管用`--XX:TargetSurvivoRatio`，即Survivor区对对象服用率，默认是50%，建议稍微讨打些。
参数`-XX:ScavengeBeforeFullGC`，即FGC前先进性一次YGC，推荐使用这个参数。

启动jar包时可以通过这样修改JVM参数
```bash
java -JVM参数 -jar application.jar
```

## 2.5 内部优化
* `zuul.host.maxTotalConnection`与`zuul。host.maxPerRouteConnections`默认值分别为200与20，如果使用`HttpClient`时则有效，如果使用的OKHttp则无效
* 对于不是很合适的Zuul中的Filter可以替换或者禁用
* zuul使用serviceId设置超时时间`ribbon.ReadTimeout`与`ribbon.SocketTimeout`生效；如果使用url映射，则应设置`zuul.host.connect-timeout-millis`与`zuul.host.socket-timeout-millis`参数


# 3 Spring Cloud Zuul原理&核心源码解析
更详细的可以查看 [Zuul Wiki](https://github.com/Netflix/zuul/wiki) 、[Spring Cloud 中文文档](https://springcloud.cc/spring-cloud-dalston.html)

## 3.1 工作原理与生命周期
Zuul的底层是由Servlet与一系列Filter组合而成，各个组件协同配合，让Zuul功能强大，且易于扩展。`ZuulServlet`通过`RequestContext`统管着由许多Filter组成的核心部件，所有操作斗鱼Filter息息相关。
HttpRequest、ZuulServlet、Filter共同构建起了Zuul的运行时生命周期


在`Finchley.RELEASE`版中Zuul使用的是`Zuul 1.3.1`的版本，当我我们在启动类上添加`@EnableZuulProxy`或者`@EnableZuulServer`之后，
```
@EnableCircuitBreaker
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ZuulProxyMarkerConfiguration.class)
public @interface EnableZuulProxy {
}


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ZuulServerMarkerConfiguration.class)
public @interface EnableZuulServer {

}
```
从这两个注解可以看到两个注解的不同之处就是前者整合了Hystrix的断路器功能。

另外就是就是前者引入的是`ZuulProxyMarkerConfiguration`，一个引入的是`ZuulServerMarkerConfiguration`，
```
/**
 * Responsible for adding in a marker bean to trigger activation of 
 * {@link ZuulProxyAutoConfiguration}
 *
 * @author Biju Kunjummen
 */

@Configuration
public class ZuulProxyMarkerConfiguration {
	@Bean
	public Marker zuulProxyMarkerBean() {
		return new Marker();
	}

	class Marker {
	}
}


/**
 * Responsible for adding in a marker bean to trigger activation of 
 * {@link ZuulServerAutoConfiguration}
 *
 * @author Biju Kunjummen
 */

@Configuration
public class ZuulServerMarkerConfiguration {
	@Bean
	public Marker zuulServerMarkerBean() {
		return new Marker();
	}

	class Marker {
	}
}
```
继续查看这两个类，代码上并无特别之处，查看注释中的@link指向的类，
```
/**
 * @author Spencer Gibb
 * @author Dave Syer
 * @author Biju Kunjummen
 */
@Configuration
@Import({ RibbonCommandFactoryConfiguration.RestClientRibbonConfiguration.class,
		RibbonCommandFactoryConfiguration.OkHttpRibbonConfiguration.class,
		RibbonCommandFactoryConfiguration.HttpClientRibbonConfiguration.class,
		HttpClientConfiguration.class })
@ConditionalOnBean(ZuulProxyMarkerConfiguration.Marker.class)
public class ZuulProxyAutoConfiguration extends ZuulServerAutoConfiguration {
    ……
}


/**
 * @author Spencer Gibb
 * @author Dave Syer
 * @author Biju Kunjummen
 */
@Configuration
@EnableConfigurationProperties({ ZuulProperties.class })
@ConditionalOnClass(ZuulServlet.class)
@ConditionalOnBean(ZuulServerMarkerConfiguration.Marker.class)
// Make sure to get the ServerProperties from the same place as a normal web app would
// FIXME @Import(ServerPropertiesAutoConfiguration.class)
public class ZuulServerAutoConfiguration {
    ……
}
```
其中注解`@ConditionalOnBean`表示的是**如果Spring上下文中存在注解中的Bean，才创建当前Bean**

`@EnableZuulServer`的底层开启的是`ZuulServerAutoConfiguration`类，它的功能是：
* 初始化配置加载器
* 初始化路由定位器
* 初始化路由映射器
* 初始化配置刷新监听器
* 初始化ZuulServlet加载器
* 初始化ZuulController
* 初始化Filter执行解析器
* 初始化一些Filter
* 初始化Metrix监控


`@EnableZuulProxy`的底层开启的是`ZuulProxyAutoConfiguration`类，这个类是由`ZuulServerAutoConfiguration`扩展而来，
所以它具有上面说的`ZuulServerAutoConfiguration`类的所有功能，同时由新增了如下的功能
* 初始化服务注册、发现监听器
* 初始化服务列表监听器
* 初始化Zuul自定义Endpoint
* 初始化一下ZuulServerAutoConfiguration中没有的Filter
* 引入HTTP客户端的两种方式：HttpClient与OKHttp

## 3.2 Filter装载与Filter链实现


