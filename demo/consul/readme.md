consul demo
----------

# Consul 安装
安装部分的官方文档 [Install Consul](https://www.consul.io/docs/install/index.html)
选择对应系统版本的Consul下载： [Download Consul](https://www.consul.io/downloads.html)

例如下载Linux 64-bit版：[consul_1.6.0_linux_amd64.zip](https://releases.hashicorp.com/consul/1.6.0/consul_1.6.0_linux_amd64.zip)

```bash
wget https://releases.hashicorp.com/consul/1.6.0/consul_1.6.0_linux_amd64.zip

# 解压
unzip consul_1.6.0_linux_amd64.zip
cd consul
# 也可以将consul路径配置到Path
export PATH=$PATH:/app/spring/consul

# 查看版本信息，如果可以正常显示版本信息，那么接下来就可以正常使用了
./consul -v

# 启动 consul
# 以开发者模式启动
consul agent -dev
# 或者启动带UI界面的服务 . 

# 方式一： 开发模式
./consul agent -dev -ui

# 方式二：绑定ip
./consul agent -dev -ui \
-bind=192.168.100.162 -client 0.0.0.0

# 方式三：集群模式
./consul agent -server -ui -bootstrap-expect=3 \
-bind=192.168.100.162 -client 0.0.0.0 \
-node=server1 \
-data-dir=/tmp/consul_data/

./consul agent -server -ui -bootstrap-expect=3 \
-bind=192.168.100.166 -client 0.0.0.0 \
-node=server2 -join 192.168.100.162 \
-data-dir=/tmp/consul_data/

./consul agent -server -ui -bootstrap-expect=3 \
-bind=192.168.100.167 -client 0.0.0.0 \
-node=server3 -join 192.168.100.162 \
-data-dir=/tmp/consul_data/

```

启动之后浏览器访问 [http://cdh6:8500](http://cdh6:8500/ui/dc1/services)


其它常用的命令
```bash
# 查看Consul代理的成员信息及它们的状态：存活、离线、启动失败
consul members

# 持续打印当前Consul日志
consul monitor

# 退出集群
consul leave

```

* http://localhost:8500/v1/agent/members  列出集群所有成员及其信息
* http://localhost:8500/v1/status/leader  显示当前集群的leader
* http://localhost:8500/v1/catalog/services  显示目前注册的服务
* http://localhost:8500/v1/kv/key            显示key对应的value
* http://localhost:8500/v1/agent/checks



# 启动项目

模块名 | 端口 | 描述
---- | ---- | ---- 
consul-provider | 8081 | consul server
consul-consumer | 8082 | consumer
Consul-config   | 8083 | 配置服务


* 启动consul服务
* 启动 consul-provider
* 启动 consul-consumer
* 访问consul管理页面：http://cdh6:8500，如果服务的Health为绿色，表示启动成功。 **注意**：Checks列不要有红色的错误提示，否则服务掉不通
* 请求接口：http://localhost:8082/hello?name=yore （**注意**：最好是本地，如果Consul不是本地一定确认配置好正确的ip地址`spring.cloud.consul.discovery.health-check-url`）

* 进入consul管理页面，点击Key/Value -> create -> Key or folder: `fconfig/consul-config/foo.bar.name`；Value: `happy`。Save
* 启动 consul-config
* 请求接口：http://localhost:8083/getName





