
CREATE DATABASE /*!32312 IF NOT EXISTS*/`test` /*!40100 DEFAULT CHARACTER SET utf8 */;

DROP TABLE IF EXISTS `zuul_route`;

-- 这个字段主要是 org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute的基础上添加的
CREATE TABLE `zuul_route` (
  `id` varchar(50) NOT NULL,
  `path` varchar(255) NOT NULL,
  `service_id` varchar(50) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `strip_prefix` tinyint(1) DEFAULT '1',
  `retryable` tinyint(1) DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert  into `zuul_route`(`id`,`path`,`service_id`,`url`,`strip_prefix`,`retryable`,`enabled`,`description`) values ('erer','/baidu/**',NULL,'http://www.baidu.com',1,0,1,'重定向百度');
insert  into `zuul_route`(`id`,`path`,`service_id`,`url`,`strip_prefix`,`retryable`,`enabled`,`description`) values ('pppp','/client/**',NULL,'http://localhost:7070',1,0,1,'url');
insert  into `zuul_route`(`id`,`path`,`service_id`,`url`,`strip_prefix`,`retryable`,`enabled`,`description`) values ('xxxx','/client-a/**','client-a',NULL,1,0,1,'serviceId');


