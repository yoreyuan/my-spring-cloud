
# 项目模块说明
模块名 | 端口 | 描述
:---- | :-----: | :----
eureka-server | 8761 | 相对来说比较简单，这里不再重复提供
client-a | 7070 | 接口服务，启动多个实例，打印出端口和结果，查看负载均衡的服务调用
ribbon-loadbalancer | 7777 | 负载均衡的客户端

## 测试
* 启动Eureka注册中心
* 以端口号7070启动client-a服务：java -jar -Dserver.port=7070  client-a-1.0-SNAPSHOT.jar
* 以端口号7075启动client-a服务：java -jar -Dserver.port=7075  client-a-1.0-SNAPSHOT.jar
* 访问负载服务接口(可以使用postman工具)，http://localhost:7777/add?a=100&b=300，多次访问查看结果信息，

在请求的结果可以看到，有端口信息可以看到服务是以轮询的方式进行请求的
```
 From Port: 7070, Result: 400
 From Port: 7075, Result: 400
 From Port: 7070, Result: 400
 From Port: 7075, Result: 400
```
