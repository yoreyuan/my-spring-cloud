Eureka
------

# 1. Eureka 的核心设计类

## InstanceInfo
在 `com.netfilx.eureka:eureka-client:1.9.2` 即 `erueka-client-1.9.2.jar`包下，`com.netflix.appinfo.InstanceInfo`类中。用来代表注册的服务实例。

字段 | 说明
---- | ----
instanceId      | 实例id
appName         | 应用名
appGroupName    | 应用所属群组
ipAddr          | ip地址
sid             | 过期，默认是"na" ，等价于 SID_DEFAULT 值
port            | 端口号
securePort      | https的端口号
homePageUrl     | 应用实例的首页url
statusPageUrl   |  
healthCheckUrl  |
secureHealthCheckUrl    |
vipAddress              |
secureVipAddress        |
statusPageRelativeUrl   |
statusPageExplicitUrl   |
healthCheckRelativeUrl  |
healthCheckSecureExplicitUrl |
vipAddressUnresolved         |
secureVipAddressUnresolved   |
healthCheckExplicitUrl       |
isCoordinatingDiscoveryServer|
……  |
asgName | 在AWS的autoscaling group的名称

##  InstanceStatus
在`InstanceInfo`类中(大概在306行左右)，有一个枚举类`InstanceStatus`，它主要用于标识服务实例的状态，


## LeaseInfo
在 `com.netfilx.eureka:eureka-client:1.9.2` 即 `erueka-client-1.9.2.jar`包下，`com.netflix.appinfo.LeaseInfo`。
它用来标识应用实例的租约信息。


## ServiceInstance
在 `org.springframework.cloud:spring-cloud-commons:2.0.0.RELAESE` 即 `spring-cloud-commons-2.-.0.RELEASE.jar`包下， 
`org.springframework.cloud.client.ServiceInstance`接口。它是Spring Cloud对 service discovery的实例信息的抽象接口，
约定了服务发现的实例应用有哪些通用的信息。


# 2. 服务的核心操作
对于服务发现来说，围绕服务实例主要有如下的几个重要操作：
* 服务注册（register）
* 服务下线（cancel）
* 服务租约（renew）
* 服务剔除（evict）

`LeaseManager`和`LookupService`是Eureka关于服务发现相关操作定义的接口类，前者定义了服务写操作相关的方法，后者定义了查询操作相关的方法。


# 3. Eureka的设计理念
作为一个服务注册及发现中心，主要解决如下几个问题：
1. 服务实例如何注册到服务中心
2. 服务实例如何从服务中心剔除
3. 服务实例信息的一致性问题

在分布式系统领域有个重要的`CAP`理论
* Consistency： 数据一致性。满足一致性原则要求需要对数据的更新操作成功之后，多副本的数据保持一致。
* Availability： 可用性。在任何时候客户端进行读写时，请求都能够正常响应。
* Partition Tolerance： 分区容错性。

在分布式系统中，一般网络因素相对不可控，所以首先会满足分区容错性(P)，在这个前提下fenbushi系统设计则在CP和AP之间进行权衡选择。
AP优于CP。

Eureka采用的是Peer to peer模式。


# 4. Eureka参数调优及监控
## Client端
Client端的参数分为基本参数、定时任务参数、http参数

* 基本参数

参数  |   默认值 | 说明
:---- | :---- | :----
eureka.client.availability-zones |  | 告知Client有哪些region及availability-zones，支持配置修改运行时生效
eureka.client.filter-only-up-instances | true | 是否过滤出InstanceStatus为UP的实例
eureka.client.region | us-east-1 | 指定该应用实例所在的region，AWS datacenters适用
eureka.client.register-with-eureka | true | 是否将该应用实例注册到 Eureka Server
eureka.client.prefer-same-zone-eureka | true | 是否优先使用与该应用实例初一相同的zone的Eureka Server
eureka.client.on-demand-update-status-change | true | 是否将本地实例状态的更新通过ApplicationInfoManager实时触发同步（有请求流控制限制）到Eureka Server
eureka.instance.metadata-map | | 指定应用实例的元数据信息
eureka.instance.prefer-ip-address | false | 是否优先使用ip地址来替代host name作为实例的hostName字段值
eureka.instance.lease-expiration-duration-in-seconds | 90 | 指定Eureka Client间隔多久需要向Eureka Server发送心跳来告知Eureka Server该实例还存活

* 定时任务参数

参数  |   默认值 | 说明
:---- | :---- | :----
eureka.client.cache-refresh-executor-thread-pool-size | 2 | 刷新缓存的CacheRefreshThread线程池大小
eureka.client.cache-refresh-executor-exponential-back-off-bound | 10 | 调度任务执行超时时下次的调度的延时时间
eureka.client.heartbeat-executor-thread-pool-size | 2 | 心跳线程HeartbeatThread线程池大小
eureka.client.heartbeat-executor-exponential-back-off-bound | 10 | 调度任务执行超市是下次的调度的延时时间
eureka.client.registry-fetch-interval-seconds | 30 | CacheRefreshThread线程的调度频率
eureka.client.eureka-service-url-poll-interval-seconds | 5*60 ❓ | AsyncResolver.updateTask数显Eureka Server地址的时间间隔
eureka.client.initial-instance-info-replication-interval-seconds | 40 | InstanceInfoReplicator将实例信息变更同步到Eureka Server的初始延时时间
eureka.client.instance-info-replication-interval-seconds | 30 | InstanceInfoREplicator将实例信息变更同步到Eureka Server的时间间隔
eureka.instance.lease-renewal-interval-in-seconds | 30 |  Eureka Client向Eureka Server发送心跳的时间间隔

* http务参数

参数  |   默认值 | 说明
:---- | :---- | :----
eureka.client.eureka-server-connect-timeout-seconds | 5 | 连接超时时间
eureka.client.eureka-server-read-timeout-seconds | 8 |  读超时时间
eureka.client.eureka-server-total-connections | 200 |  连接池最大活动连接数（MaxTotal）
eureka.client.eureka-server-total-connections-per-host | 50 | 每个host能使用的最大连接数
eureka.client.eureka-connection-idle-timeout-seconds | 30 | 连接池中连接的空闲时间

## Server端

* 基本参数

参数  |   默认值 | 说明
:---- | :---- | :----
eureka.server.enable-self-preservation | true | 是否开启自我保护模式
eureka.server.renewal-percent-threshold | 0.85 | 指定每分钟需要收到的续约次数的阈值
eureka.instance.registry.expected-number-of-renews-per-min | 1 | 指定每分钟需要收到的续约次数值，实际改制在其中被写死为count*2，另外也会被更新
eureka.server.renewal-threshold-update-interval-ms | 15分钟 ❓ | 指定updateRenewalThreshold定时任务的调度频率，来动态更新expectedNumberOfRenewsPerMin及numberOfRenewsPerMinThreshold值
eureka.server.eviction-interval-timer-in-ms | 60*1000 | 指定EvictionTask定时任务的调度频率，用于剔除过期的实例


* response cache 参数

参数  |   默认值 | 说明
:---- | :---- | :----
eureka.server.use-read-only-response-cache | true | 是否使用只读的response-cache
eureka.server.response-cache-update-interval-ms | 30*1000❓ | 设置CacheUpdateTask的调度时间间隔
eureka.server.response-cache-auto-expiration-in-seconds | 180 | 是指readWriteCacheMap的expireAfterWrite参数，指定写入多长时间后过期

* peer 参数

参数  |   默认值 | 说明
:---- | :---- | :----
eureka.server.peer-eureka-nodes-update-interval-ms | 10分钟 |
eureka.server.peer-eureka-status-refresh-time-interval-ms | 30*1000 |

* http 参数

参数  |   默认值 | 说明
:---- | :---- | :----
eureka.server.peer-node-connect-timeout-ms | 200 | 连接超时时间
eureka.server.peer-node-read-timeout-ms | 200 |  读超时时间
eureka.server.peer-node-total-connections | 1000 | 连接池最大活动连接数
eureka.server.peer-node-total-connections-per-host | 500 | 每个host能使用的最大连接数
eureka.server.peer-node-connection-idle-timeout-seconds | 30 | 连接池中连接的空闲时间


## 参数优化
1. 问题： 为什么服务下线了，Eureka Server接口返回的信息还会存在
因为Eureka Server并不是强一致性的，因此registry中会存留过期的实例信息。
①应用实例异常挂掉，没能在挂掉之前告知Eureka Server要下线掉该服务实例信息。需要依赖Eureka Server的EvictionTask去剔除。
②应用实例下线时有告知Eureka Server下线，但由于Eureka Server的REST API有response cache，因此需要等待缓存过期才能更新
③Eureka Server开启了`SELF PRESERVATION`模式。知道退出此模式

2. 问题： 为什么服务上线了，Eureka Client不能及时获取到

3. 问题： 为什么有时候会出现如下错误提示：`EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE`

上面的几个问题可以关注如下几个配置项进行解决
```bash
#调度间隔时间从默认的60秒，改为5秒
eureka.server.eviction-interval-timer-in-ms=5000
# response cache问题，可以关闭readOnlyCacheMap
eureka.server.use-read-only-response-cache=false
# 或者调整过期时间
eureka.server.response-cache-auto-expiration-in-seconds=60
# 针对SELF PRESERVATION问题，测试换进可以将 enable-self-preservation 设置为false
eureka.server.enable-self-preservation=false
# 针对新服务获取不及时，测试环境可以适当提高Client端拉取Server注册信息的频率。例如将下面的默认的30秒改为5秒
eureka.client.registry-fetch-interval-seconds=5
# 在生产换进可以将如下两个参数调小点，提高触发 SELF PRESERVATION 机制的门槛。30->10；0.85->0.45
eureka.instance.lease-renewal-interval-in-seconds=10
eureka.server.renewal-percent-threshold=0.45

```

## 指标监控
Eureka内置了基于servo的指标统计，具体可查看 eureka-core-1.9.2.jar 的 `com.netflix.eureka.util.EurekaMonitors`类。
Spring Boot 2.x版本改为使用Micrometer，不再支持Netflix Servo，转而支持了Netflix Servo的替代者Netflix Spectator。
不过可以通过`DefaultMonitorRegistry.getInstace().getRegisteredMonitors()`来获取所有注册了的Monitor，进而获取其指标值。


# 5 Eureka 实战
这个命令启动需要每个模块引入`spring-boot-maven-plugin` ：mvn spring-boot:run -Dspring.profiles=active=peer2  
java -jar eureka-server-1.0-SNAPSHOT.jar --spring.profiles.active=peer1
-Dserver.port=9999

启动 config-server、eureka-server、eureka-client

当启动多个 eureka-server时，其实就是多个server之间相互注册，然后eureka-client注册到多个eureka-server上，


测试进行如下请求
```bash
# 刷新eureka-server1配置
curl -i -X POST  cdh6:8761/actuator/refresh

# 调用接口 /query/eureka-server
curl -i http://cdh6:8761/query/eureka-server
curl -i http://localhost:8081/query/eureka-server
```

## Multi Zone Eureka Server
通过`eureka.instance.metadataMap.zone`设置每个实例所属的zone。

启动实例服务：`java -jar eureka-server-1.0-SNAPSHOT.jar --spring.profiles.active=zone1a`

启动 gatwway: `java -jar zuul-server-1.0-SNAPSHOT.jar --spring.profiles.active=zone1`

请求：`http://localhost:10001/client/actuator/env`

## 开启 HTTP Basic 认证
在实际生产部署中，需要考虑一个安全问题，比如Eureka Server自己有暴露REST API，如果没有安全认证，别人就可以通过REST API随意修改信息，

引入如下配置，并进行配置：
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
```

启动：mvn spring-boot:run -Dspring.profiles=active=security 

验证。可以打开eureka浏览器，也可以请求：`curl -i http://localhost:8761/eureka/apps`
```bash
# 当什么参数都不传入是，会报401
curl -i http://localhost:8761/eureka/apps

# 传入参数，进行验证
curl -i --basic -u admin:Xk38CNHigBP5jK75 http://localhost:8761/eureka/apps

```

启动eureka client：mvn spring-boot:run -Dspring.profiles=active=security 

## 启用 https
* 证书生成
```bash
keytool -genkeypair -alias server -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore server.p12 -validity 3650
#输入密钥库口令(6个字符以上)：server
#再次输入新口令：
#你的名字与姓氏：
#你的组织单位名称是什么：
#你的组织名称是什么：
#你所在的城市区域名称是什么
#你所在的省/市/自治区名称是什么？
#改单位的双字幕国家/地区代码是什么？
# Y
```
这一步会在当前目录下生成`server.p12`文件

* 生成client端使用的证书
```bash
keytool -genkeypair -alias client -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore client.p12 -validity 3650
# 输入密钥库口令: client
# ……
```
这一步会生成`client.p12`文件

* 导出两个p12证书
```bash
keytool -export -alias server -file server.crt --keystore server.p12
# 输入密钥库口令(生成server.p12时的密码)：server
# Certificate stored in file <server.crt>
```

```bash
keytool -export -alias client -file client.crt --keystore client.p12
# 输入密钥库口令(生成client.p12时的密码)：client
# Certificate stored in file <client.crt>
```

* 将server.crt文件导入client.p12中，是Client端信任Server的证书：
```
[root@cdh6 .su]# keytool -import -alias server -file server.crt -keystore client.p12
Enter keystore password:（client.p12的密码） client
Owner: CN=yore, OU=a, O=a, L=bj, ST=bj, C=cn
Issuer: CN=yore, OU=a, O=a, L=bj, ST=bj, C=cn
Serial number: 45ad26d6
Valid from: Thu Jul 03 23:35:58 CST 2019 until: Sun Jul 01 23:35:58 CST 2029
Certificate fingerprints:
         MD5:  7B:50:28:21:09:6E:81:78:6D:2A:5A:88:E8:71:9D:96
         SHA1: ED:86:F2:98:90:C8:8E:86:74:76:C3:EE:98:39:FF:E2:F6:CC:40:5E
         SHA256: DE:43:FD:F6:CF:F4:08:99:4E:D6:65:6E:9F:16:CF:6F:61:5F:EE:A7:C8:CC:F4:26:93:87:73:D7:61:9F:B9:28
Signature algorithm name: SHA256withRSA
Subject Public Key Algorithm: 2048-bit RSA key
Version: 3
Extensions:
#1: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: E3 AE 1D 48 5B 04 3D 6C   87 CA A1 7C FF 05 D1 2B  ...H[.=l.......+
0010: 10 5C E4 E7                                        .\..
]
]
Trust this certificate? [no]:  Y
Certificate was added to keystore
```
从上面可以看到证书的信任是有有效期的，大概有10年有效期

* 将client.crt导入server.p12中，使得Server端信任Client的证书：
```
keytool -import -alias client -file client.crt -keystore server.p12
Enter keystore password:（server.p12的密码） server

```

* 将生成的`server.p12`放到Maven工程下的eureka-server的resources目录下，然后指定相关配置

* 将生成的`client.p12`放到Maven工程下的eureka-client的resources目录下，然后指定相关配置

* 启动
```bash
mvn spring-boot:run -Dspring.profiles=active=https 
```

* 查看
```bash
curl --insecure https://localhost:8761/eureka/apps
# 如果有密码，使用如下命令
curl --insecure --basic -u admin:admin https://localhost:8761/eureka/apps

```

## Eureka Admin
在Spring Cloud技术栈中，Eureka Server是微服务架构中必不可少的基础组件，所以Spring Cloud中国社区开放了Eureka Server在线服务供大家学习和测试。
[访问地址](http://eureka.springcloud.cn)

Spring Cloud 中国社区为Eureka服务注册中心开源了一个节点监控、服务动态启停的管控平台：Eureka Admin，
GitHub地址为 [https://github.com/SpringCloud/eureka-admin](https://github.com/SpringCloud/eureka-admin) 。
目前因为包未提交到中央仓库，因此需要自己构建源码，

```bash
# clone 源码 
git clone https://github.com/SpringCloud/eureka-admin.git
cd eureka-admin/
```


配置项目的配置文件: `eureka-admin-starter-server/src/main/resources/bootstrap.yml`,根据自己情况进行设置，
主要设置eurek祖册中心的地址和端口号。
```
server:
  port: 18080
eureka:
  server: 
    eviction-interval-timer-in-ms: 30000
  client:
    register-with-eureka: false
    fetch-registry: true
    filterOnlyUpInstances: false
    serviceUrl:
       defaultZone: http://localhost:8888/eureka/
logging:
  level:
    com:
      itopener: DEBUG
    org:
      springframework: INFO

```

然后启动主类
```bash
# 启动
cd eureka-admin-starter-server
mvn spring-boot:run
# java -cp eureka-admin-starter-server/target/eureka-admin-starter-server-0.0.1-SNAPSHOT.jar cn.springcloud.eureka.EurekaAdminServer >/dev/null 2>&1 &

```

浏览器访问页面： http://loclhost:18080/


# 6 Eureka故障处置
## Eureka Server全部不可用
当应用服务启动之前Eureka Server 未启动或者挂了，此时启动应用服务可以正常启动，但是会报一些请求执行的错误信息

如果是应用服务运行时Eureka Server不可用, Eureka Client在定时拉取Server端注册信息时，会有异常输出

## Eureka Server部分不可用
* Client 端
Client端第一次拉取的时候hi按照配置的顺序，之后会随机更新。Client请求Server的时候，维护了一个不可用的Eureka Server列表

