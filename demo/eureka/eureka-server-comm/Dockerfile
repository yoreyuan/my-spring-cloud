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
