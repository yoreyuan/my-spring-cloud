[Spring Cloud Config](https://github.com/spring-cloud/spring-cloud-config)
==========
Spring Cloud Config是Spring Cloud微服务体系中的配置中心，是微服务不可或缺的一部分，其能够很好地将程序中配置日益增多的各种功能的开关、参数的配置、
服务器的地址，配置修改后实时生效、灰度发布、分环境、分集群管理配置等进行全面的集中化管理，有利于系统的配置管理和维护。

# 1 配置中心概述
## 1.1 什么是配置中心
在互联网时代，应用都是以分布式系统，部署在N台服务器上，想要去线上一台台地重启机器肯定不靠谱，并且维护成本很高，所以配置中心应运而生。

配置中心被用作集群中管理不同环境和不同集群配置，以及在修改配置后将实时动态推送到应用上进行刷新。

## 1.2 Spring Cloud Config
它是一个集中化外部配置的分布式系统，有服务端和客户端组成。它不依赖于注册中心，是一个独立的配置中心。

它支持多种存储配置信息的形式，目前主要有jdbc、Vault、Native、svn、git，默认为git。


# 2 刷新配置中心信息

`@RefreshScope`，被这个注解修饰的Bean都是延迟加载的，只有在第一次访问才会被初始化，刷新Bean也是同理，下次访问时会创建一个新的对象。

配置完毕后，需要访问 http://localhost:config-client-port/actuator/refresh

## 2.2 结合 Spring Cloud Bus 热刷新
用户更新配置信息时，检查到Git Hook变化，触发Hook配置地址和调用，Config Server接收到请求并发布消息，Bus消息发送到Config Client

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://gitee.com/zhongzunfa/spring-cloud-config.git
          #username:
          #password:
          search-paths: SC-BOOK-CONFIG

    bus:
      trace:
        enabled: true

  application:
    name: config-server-bus

  ## 配置rabbitMQ 信息
  rabbitmq:
      host: localhost
      port: 5672
      username: guest
      password: guest

server:
    port: 9090
    
```









