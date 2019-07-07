
# 项目模块说明
模块名 | 端口 | 描述
:---- | :-----: | :----
eureka-server | 8761 | 相对来说比较简单，这里不再重复提供
feign-file-server | 8012 | 模拟文件服务器，服务提供者
feign-upload-client | 8011 | 文件表单上传，通过Feign Client发送文件到文件服务器

# 启动
* 先启动注册中心
* 启动文件上传服务：feign-file-server
* 启动文件上传客户端：feign-upload-client

* 访问： http://localhost:8011/swagger-ui.html

