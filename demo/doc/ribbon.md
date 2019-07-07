Ribbon
---
Spring Cloud 的 Ribbon是一个客户端负载均衡器

# 概述
## 项目中使用引入
在pom中引入如下依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

在启动类中需要注入RestTemplate的Bean，并添加注解`@LoadBalanced`，声明该RestTemplate用于负载均衡。

将服务配置到注册中心


# 实战
## Ribbon负载均衡策略与自定义配置
策略名 | 命名 | 描述
:---- | ---- | ----
RandomRule                | 随机策略        | 随机选择Server
RoundRobinRule            | 轮询策略（默认） | 按顺序循环选择Server
RetryRule                 | 重试策略        | 在一个配置时间段内选择server不成功，则一直尝试选择一个可用的server
BestAvailableRule         | 最低并发策略     | 逐个考察server，如果server断路器打开，则忽略，在选择其中并发连接最低的server
AvailabilityFilteringRule | 可用过滤策略     | 过滤掉一直连接失败并标记为circuit tripped的server，过滤掉高并发连接的server（active connections超过配置的阈值）
ResponseTimeWeightedRule  | 响应时间加权策略 | 根据server的响应时间分配权重。响应时间越长，权重越低，被选择到的概率就越低。这个策略和贴切，综合了各种因素，如：网络、磁盘、IO等，这些因素直接影响着响应时间
ZoneAvoidanceRule         | 区域权衡策略     | 综合判断server所在区域的性能和server的可用性轮询选择server，并且判定一个AWS Zone的运行性能是否可用，剔除不可用的Zone中的所有Server


### 全局策略的设置
添加一个配置类,再次请求负载均衡策略就更改为了随机策略
```java
@Configuration
public class TestConfiguration {
	@Bean
	public IRule ribbonRule() {
		return new RandomRule();
	}
}
```

### 基于注解的策略配置
如果需要针对一个服务设置其特有的策略，可以使用`@RibbonClient`注解。

* 修改配置类，`@AvoidScan`注解是一个空声明：
```java
@Configuration
@AvoidScan
public class TestConfiguration {
    @Autowired
    IClientConfig config;

    @Bean
    public IRule ribbonRule(IClientConfig config) {
        return new RandomRule();
    }
}
```

* 在启动类上加上`@RibbonClient`注解，其中`@ComponentScan`注解是为了让Spring不去扫描被`@AvoidScan`注解的配置类
```
@RibbonClients(value = {
		@RibbonClient(name = "client-a", configuration = TestConfiguration.class),
//		@RibbonClient(name = "client-b", configuration = TestConfiguration.class)
})
@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = {AvoidScan.class})})
```

### 基于配置文件的策略设置
```yaml
client-a:
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule#
```

## Ribbon 超时与重试
F版的Ribbon重试机制是默认开启的，需要添加对于超时时间与重试策略的配置，如下：
```yaml
client-a:
  ribbon:
    ConnectTimeout: 3000
    ReadTimeout: 60000
    MaxAutoRetries: 1 #对第一次请求的服务的重试次数
    MaxAutoRetriesNextServer: 1 #要重试的下一个服务的最大数量（不包括第一个服务）
    OkToRetryOnAllOperations: true
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule#
```

## Ribbon的饥饿加载
Ribbon在进行客户端负载均衡的时候并不是在启动时就加载上下文，而是在实际请求的时候才会去创建，因此这个特性往往也会让我们在第一次调用显得疲软乏力，
严重的时候会引起调用超时，所以我们可以通过指定Ribbon具体的客户端的名称来启动饥饿加载，即在启动的时候便加载所有配置项的应用程序上下文
```yaml
ribbon:
  eager-load:
    enabled: true
    clients: client-a, client-b, client-c
```

## 利用配置文件自定Ribbon客户端
Ribbon在1.2.0之后，就可以使用配置文件来定制Ribbon客户端了，其实质也就是使用配置文件来指定一些默认加载类，从而更改Ribbon客户端的行为方式，并且使用这种方式优先级最高。

配置项 | 说明
---- | ----
\<clientName>.ribbon.NFLoadBalancerClassName | 指定ILoadBalancer的实现类
\<clientName>.ribbon.NFLoadbalancerRuleClassName | 指定IRule的实现类
\<clientName>.ribbon.NFLoadBalancerPingClassName | 指定IPing的实现类
\<clientName>.ribbon.NIWSServerListClassName | 指定ServerList的实现类
\<clientName>.ribbon.NIWSServerListFilterClassName | 指定ServerListFilter的实现类

例如对client的原服务定制的配置示例：
```yaml
client: 
  ribbon:
    NIWSServerListClassName: com.netflix.loadbalancer.ConfigurationBasedServerList
    NFLoadbalancerRuleClassName: com.netflix.loadbalancer.WeightedResponseTimeRule
```

## Ribbon 脱离Eureka的使用
如果Eureka注册中心时一个公共的，在获取服务注册信息列表时可以直接在Ribbon客户端指定的服务列表中获取。配置如下
```yaml
ribbon:
  eureka: 
    enbaled: false

client:
  ribbon:
    listOfServers: http://localhost:7070,http://localhost:7071
```


# 进阶
Ribbon核心接口

接口 | 描述 | 默认实现类
:---- | :---- | :---- 
IClientConfig | 定义Ribbon中管理配置的接口 | DafaultClientConfigImpl
IRule | 定义Ribbon中负载均衡策略的接口 | ZoneAvoidanceRule
IPing | 定义定期ping服务检查可用性的接口 | DummyPing
ServerList\<Server> | 定义获取服务列表方法的接口 | ConfigurationBasedServerList
ServerListFilter\<Server> | 定义特定期望获取服务列表方法的接口 | ZonePreferenceServerListFilter
ILoadBalancer | 定义负载均衡选择服务的核心方法接口 | ZoneAwareLoadBalancer
ServerListUpdater | 为DynamicServerListLoadBalancer定义动态更新服务列表的接口 | PollingServerListUpdater




























