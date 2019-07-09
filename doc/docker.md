Spring 项目中 Docker 的使用
----

# 目录 
* 1 安装
	+ 1.1 Mak OS 下安装
	+ 1.2 Centos 下的安装
		- 1.2.1 如果有旧版本，先卸载
		- 1.2.2 更新系统包
		- [1.2.3 安装](#1.2.3)
			* [1.2.3.1 便捷脚本形式安装](#1.2.3.1)
				+ 1 下载脚本
				+ 2 执行脚本
				+ 3 非root用户运行docker
			* [1.2.3.2 RMP包形式安装](#1.2.3.2)
				+ 1 下载
				+ 2 安装Docker CE
				+ 3 查看docker信息
				+ 4 启动Docker
				+ 5 查看Docker信息
		- [2.2.4 配置为国内镜像](#2.2.4)
* [2 Spring Cloud 中的使用](#2)
	+ 2.1 Docker 服务器设置
		- 2.1.1 生成 key 
		- 2.1.2 crt文件 
		- 2.1.3 添加证书到Centos7
		- 2.1.4 重启Docker
		- 2.1.5 创建仓库的私服
		- 2.1.6 开启Remote API
		- 2.1.7 重新加载文件重启docker
		- 2.1.8 验证
	+ [2.2 IDEA 设置](#2.2)
	+ [2.3 项目中设置](#2.3)
	+ [2.4 提交运行](#2.4)
	+ [2.5 查看容器的运行日志](#2.5)
	+ 2.6 关闭容器
	+ 2.7 docker-compose
		- 2.7.1 安装
		- 2.7.2 使用



# 1. 安装
[官网：](https://www.docker.com/get-started)  
[文档](https://docs.docker.com/)
[Centos 7 Docker CE 官方安装文档](https://docs.docker.com/install/linux/docker-ce/centos/)

## 1.1 Mak OS 下安装
mac系统下安装Docker是比较简单的，大体上安装步骤为：下载、安装、配置。更详细的可以参考我的一篇博客 [Mac系统下Docker方式安装Mysql](https://blog.csdn.net/github_39577257/article/details/82955623)

## 1.2 Centos 下的安装
Docker一般都要气系统为64位，当为Centos7时内核不能小于 3.10。这里以安装**Docker Community Edition**。
```bash
[root@cdh6 ~]# uname -r
3.10.0-693.el7.x86_64
```

如果不是，可以通过阿里云下载 [http://mirrors.aliyun.com/centos/](http://mirrors.aliyun.com/centos/)安装Centos 7系统，
点击进入需要下载的版本目录，然后进入 `isos/x86_64/`，会看到有很多的版本，如何选择，可以查看其中的`0_README.txt`文件，这里对每个版本进行的比较详细的说明，根据自己情况下载安装配置环境。

### 1.2.1 如果有旧版本，先卸载
```bash
sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
                  
# 移除docker-ce
sudo yum remove docker-ce
# 删除Docker的镜像、容器、卷或自定义配置文件
sudo rm -rf /var/lib/docker
```

### 1.2.2 更新系统包
```bash
yum -y update
```

### 1.2.3 安装
对于安装Docker CE，官方文档给出了3中方式安装：
1. **使用repository安装**：大多数用户[设置Docker的存储库](https://docs.docker.com/install/linux/docker-ce/centos/#install-using-the-repository)并从中进行安装，以便于安装和升级任务。这是推荐的方法。
2. **手动安装**：有些用户下载RPM软件包并[install it manually](https://docs.docker.com/install/linux/docker-ce/centos/#install-from-a-package)并完全手动管理升级。这在诸如没有访问互联网的气隙系统(air-gapped systems)上安装Docker的情况下非常有用。
3. **便携脚本安装**：在测试和开发环境中，一些用户选择使用自动[convenience scripts](https://docs.docker.com/install/linux/docker-ce/centos/#install-using-the-convenience-script)来安装Docker。

下面主要介绍后两种安装方式，在测试环境可以使用脚本快速安装，在无法联网的情况下则可以通过手动下载rpm包的方式安装

### 1.2.3.1 便捷脚本形式安装
这种方式建议不要在生产环境中使用，总之脚本会自动配置包管理且安装时不会有任何建议和提示，会安装大量软件包，时间较慢。

使用get.docker.com上的脚本在Linux上安装最新版本的Docker CE。要安装最新的测试版本，请改用test.docker.com。在下面的每个命令中，替换每次出现的getwith test。

#### 1 下载脚本
```bash
# 如果需要安装最新的测试版本，地址可以改为 test.docker.com
curl -fsSL https://get.docker.com -o get-docker.sh
```

#### 2 执行脚本
```bash
# 执行前请确保脚本有执行的权限
sudo sh get-docker.sh
```

#### 3 非root用户运行docker
```bash
sudo usermod -aG docker your-user
```

### 1.2.3.2 RMP包形式安装
浏览器访问： [https://download.docker.com/linux/centos/7/x86_64/](https://download.docker.com/linux/centos/7/x86_64/)，选择`stable`稳定版，也可以选择其他版本。

#### 1. 下载
这里下载 [docker-ce-18.06.3.ce-3.el7.x86_64.rpm](https://download.docker.com/linux/centos/7/x86_64/stable/Packages/docker-ce-18.06.3.ce-3.el7.x86_64.rpm)

#### 2. 安装Docker CE，
将下面的路径更改为您下载Docker rpm包的路径
```bash
sudo yum install /path/to/docker-ce-18.06.3.ce-3.el7.x86_64.rpm
```

#### 3. 查看docker信息
```bash
docker --version
``` 

#### 4. 启动Docker
```bash
sudo systemctl start docker

# 可以查看启动状态
sudo systemctl status docker
```

#### 5. 查看Docker信息
```bash
docker info 
```

### 2.2.4 配置为国内镜像
这里配置为阿里的镜像.访问阿里云官网[https://www.aliyun.com/](https://www.aliyun.com/)登陆账号，进入管理控制台 --> 产品与服务 --> 弹性计算 --> 容器镜像服务 --> 镜像加速器，复制自己的专属加速器地址

```
vim /etc/docker/daemon.json

# 添加自己的镜像地址，保存退出
{
  "registry-mirrors": ["https://xxxxxxxx.mirror.aliyuncs.com"]
}
```

重新加载文件重启docker
```bash
systemctl daemon-reload
systemctl restart docker
```

<br/>

*********

<br/>

# 2 spring Cloud 中的使用
下面使用的 hostname.com 更换为自己的域名

## 2.1 Docker 服务器设置
### 2.1.1 生成 key 
执行下面命令生成 hostname.com.key 文件
```bash
openssl genrsa -out hostname.com.key 2048
```

### 2.1.2 crt文件 
执行下面命令生成 hostname.com.crt 文件
```bash
openssl req -newkey rsa:4096 -nodes -sha256 -keyout hostname.com.key -x509 -days 3650 -out hostname.com.crt
# 此时会提示需要我们填入一些信息
# （国名）Country Name (2 letter code) [XX]: cn
# （省份）State or Province Name (full name) []: bj
# （城市）Locality Name (eg, city) [Default City]: bj
# （组织名）Organization Name (eg, company) [Default Company Ltd]: com
# （组织单位）Organizational Unit Name (eg, section) []: java
# （服务名） (eg, your name or your server's hostname) []: hostname.com
# （邮箱地址）Email Address []: root@cdh6.com
```

### 2.1.3 添加证书到Centos7
自签名证书不受docker信任，需要添加到Docker根证书中，Centos 7的存放路径：`/etc/docker/certs.d/域名`

```bash
mkdir -p /etc/docker/certs.d/hostname.com
cp cdh6.crt /etc/docker/certs.d/hostname.com/
```

### 2.1.4 重启Docker
```bash
systemctl restart docker
```

### 2.1.5 创建仓库的私服
```bash
docker run -d -p 443:5000 --restart=always --name registry -v `pwd`/certs:/certs -v /opt/docker-image:/opt/docker-image -e STORAGE_PATH=/opt/docker-image -e REGISTRY_HTTP_TLS_CERTIFICATE=/certs/hostname.com.crt -e REGISTRY_HTTP_TLS_KEY=/certs/hostname.com.key registry:2
```
一些参数解释：
* `pwd`/certs:/certs --- 将当前目录(cd ~)下的certs挂载到容器的certs目录
* -v /opt/docker-image:/opt/docker-image --指定存储镜像路径，防止私有仓库容器被删，镜像也丢失
* -e REGISTRY_HTTP_TLS_CERTIFICATE和-e REGISTRY_HTTP_TLS_KEY 指定证书路径


### 2.1.6 开启Remote API
编辑 `/usr/lib/systemd/system/docker.service` 文件，主要修改 ExecStart 
```bash
ExecStart=/usr/bin/dockerd -H unix:///var/run/docker.sock -H tcp://0.0.0.0:2375
```

### 2.1.7 重新加载文件重启docker
```bash
systemctl daemon-reload
systemctl restart docker
```

### 2.1.8 验证
浏览器输入docker服务器地址 `http://hostname.com:2375/images/json`，可以看到镜像信息（docker images）


## 2.2 IDEA 设置
* 安装docker插件： <kbd> IntelliJ IDEA </kbd> -> <kbd> Preferences </kbd> -> <kbd> Plugins </kbd> -> <kbd> Install JetBrains plugin  </kbd>

* 重启 IDEA

* 设置：<kbd> IntelliJ IDEA </kbd> -> <kbd> Preferences </kbd> -> <kbd> Build,Execution,Deployment </kbd> -> <kbd> Docker </kbd>

* 点击`+` ，然后选中 TCP socket ，配置 `Engine API URL `和` Certificates folder`，

## 2.3 项目中设置
在pom中加入docker插件，并进行如下配置

```
<!-- maven docker 插件 -->
<plugin>
    <groupId>com.spotify</groupId>
    <artifactId>docker-maven-plugin</artifactId>
    <version>1.2.0</version>
    <!--将插件绑定在某个phase执行-->
    <executions>
        <execution>
            <id>build-image</id>
            <!--将插件绑定在package这个phase上。用户只需执行mvn package ，就会自动执行mvn docker:build-->
            <phase>package</phase>
            <goals>
                <goal>build</goal>
            </goals>
        </execution>
    </executions>

    <configuration>
        <!-- imageName一定要符合规则[a-z0-9-_.] -->
        <imageName>${project.artifactId}:${project.version}</imageName>
        <!--指定标签-->
        <imageTags>
            <imageTag>latest</imageTag>
        </imageTags>
        <!-- 指定 Dockerfile 路径  ${project.basedir}：项目根路径下-->
        <dockerDirectory>${project.basedir}</dockerDirectory>
        <!--指定远程 docker api地址-->
        <dockerHost>http://cdh6:2375</dockerHost>
        <resources>
            <resource>
                <directory>${project.build.directory}</directory>
                <include>${project.build.finalName}.jar</include>
            </resource>
        </resources>
    </configuration>
</plugin>
```

在项目的根目录下创建 DockerFile
```dockerfile
## 基于java 8镜像
FROM java:8

## 将本地文件挂在到当前容器
VOLUME /opt

## 拷贝文件内容
ADD eureka-server-comm-1.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'

## 容器启动后命令
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

## 开放端口
EXPOSE 8761
```



## 2.4 提交运行
```bash
docker run --name eureka-server  -d -p 8761:8761 -t eureka-server-comm:latest
```

## 2.5 查看容器的运行日志
```bash
# 查看运行的容器
docker ps

# 查看容器中的文件列表
 docker container  diff 容器id

# 根据上面的容器id，查看日志
docker logs -f -t --tail 函数 容器id

# 在容器环境下执行命令
docker exec -it containerID /bin/bash

```

## 2.6 关闭容器
```bash
docker ps
# 关闭某个容器
docker stop 容器id

# 如果镜像不需要，可以删除镜像
docker rmi 镜像id
# 如果删除时提示： Error response from daemon: conflict: unable to delete a6e60a1bc53b (must be forced) - image is referenced in multiple repositories
docker rmi  -f  容器id
# 如果启动服务是报错，则根据提示的容器id，直接shanchu：docker: Error response from daemon: Conflict. The container name "/eureka-server" is already in use by container "a80557e5613a8bf1cebe7074655d8916bd61a9f3688ee1120e900ce2c7265cbb". You have to  that container to be able to reuse that name.
docker rm 容器id

```

## 2.7 docker-compose
如果项目模块比较多，可以使用docker-compose。
### 2.7.1 安装
```bash
wget https://github.com/docker/compose/releases/download/1.24.1/docker-compose-Linux-x86_64
mv docker-compose-Linux-x86_64 /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
docker-compose --version
```

### 2.7.2 使用
这部分可以查看其他资料，或者官方文档[Docker Compose](https://docs.docker.com/compose/overview/)


<br/><br/><br/>

更多可查看我的博客 [Spring Cloud 项目中 Docker 的使用](https://blog.csdn.net/github_39577257/article/details/95250521)

<br/>

