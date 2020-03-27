# rookie
Spring-Boot 后端项目练习
## 目的
熟悉Spring Boot常用组件，了解开发流程。



# 项目记录起步

## 1. 项目框架搭建

​	使用maven管理各种依赖，那么首先就需要先创建一个maven项目，并且将这个初创建的项目作为整个工程的最顶层目录。然后以此为基础，创建几个常用的模块，模块之间相互依赖。

![](https://yxin-images.oss-cn-shenzhen.aliyuncs.com/img/Snipaste_2020-03-22_19-09-32.jpg)

### 1. 构建聚合工程

**（聚合工程 = 父工程 + 子工程（模块））**

​	子工程之间是平级的模块，子模块如果要使用资源，必须构建依赖。

> 1. 聚合工程里可以分为顶级项目（顶级工程、父工程）与子工程，这两者的关系其实就是父子继承的关系
>    子工程在maven里称之为模块（module），模块之间是平级，是可以相互依赖的。
> 2. 子模块可以使用顶级工程里所有的资源（依赖），子模块之间如果要使用资源，必须构建依赖（构建关系）
> 3. 一个顶级工程是可以由多个不同的子工程共同组合而成。

1. 先创建maven的顶层结构 `rookie-star`。作为父级结构，设置他的pom文件，加上如下配置

```xml
<!--最外层顶层项目，使用的打包方式-->
<packaging>pom</packaging>
<!--指定项目编码方式，执行Java版本-->
<properties>
    <project.build.sourceEnconding>UTF-8</project.build.sourceEnconding>
    <project.reporting.outputEnconding>UTF-8</project.reporting.outputEnconding>
    <java.version>1.8</java.version>
</properties>
```

2. 分别创建`rookie-common`，`rookie-pojo`，`rookie-mapper`，`rookie-service`，`rookie-api`几个模块，他们之间的相互依赖关系可以描述为

```
// 依次向后依赖
api -> service -> mapper -> pojo -> common
```

3. 因此，在各自的对应`pom`文件中需要引入依赖关系。举个栗子，如下为`api`模块的`pom`文件。

```xml
<dependency>
     <!--
         api -> service -> mapper -> pojo -> common
         依次向后依赖
     -->
     <groupId>com.rookie</groupId>
     <artifactId>rookie-service</artifactId>
     <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 安装各个模块

1. 在父工程中的`maven`管理中，使用`install`安装各个子模块

![](https://yxin-images.oss-cn-shenzhen.aliyuncs.com/img/Snipaste_2020-03-22_19-26-57.jpg)

### 3. 测试

1. `rookie-api`中创建`yml`文件
2. 在rookie-api中新建一个主启动类

![](https://yxin-images.oss-cn-shenzhen.aliyuncs.com/img/Snipaste_2020-03-22_19-49-35.jpg)

3. 同理，新建一个`controller`。

![](https://yxin-images.oss-cn-shenzhen.aliyuncs.com/img/Snipaste_2020-03-22_19-50-05.jpg)

4. 使用`maven`父工程安装`install`一下。然后启动项目，在浏览器输入`localhost:8080/hello`就能够看到页面正常显示`Hello World` 了。

## 2. 配置数据源，整合HikariCP

1. 父工程项目中引入`mysql`和`mybatis`驱动

```xml
<!-- mysql驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.41</version>
</dependency>
<!-- mybatis -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>
```

2. `rookie-api`中配置`application.yml`

```yaml
spring:
#  profiles:
#    active: dev
  datasource:                                           # 数据源的相关配置
    type: com.zaxxer.hikari.HikariDataSource          # 数据源类型：HikariCP
    driver-class-name: com.mysql.jdbc.Driver          # mysql驱动
    url: jdbc:mysql://localhost:3306/rookie-star?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
    username: root
    password: root
    hikari:
      connection-timeout: 30000       # 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 默认:30秒
      minimum-idle: 5                 # 最小连接数
      maximum-pool-size: 20           # 最大连接数
      auto-commit: true               # 自动提交
      idle-timeout: 600000            # 连接超时的最大时长（毫秒），超时则被释放（retired），默认:10分钟
      pool-name: DateSourceHikariCP     # 连接池名字
      max-lifetime: 1800000           # 连接的生命时长（毫秒），超时而且没被使用则被释放（retired），默认:30分钟 1800000ms
      connection-test-query: SELECT 1
  servlet:
    multipart:
      max-file-size: 512000     # 文件上传大小限制为500kb
      max-request-size: 512000  # 请求大小限制为500kb

############################################################
#
# mybatis 配置
#
############################################################
mybatis:
  type-aliases-package: com.rookie.pojo          # 所有POJO类所在包路径，需要创建对应的文件夹
  mapper-locations: classpath:mapper/*.xml      # mapper映射文件，需要创建mapper文件夹
#  configuration:
#    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

```

## 3. MyBaties逆向生成工具

使用逆向生成工具生成数据库mapper以及一些pojo

1. 父工程中引入通用mapper逆向工具

```xml
<!--通用mapper逆向工具-->
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper-spring-boot-starter</artifactId>
    <version>2.1.5</version>
</dependency>
```

2. 在yml中引入通用mapper配置

```yml
############################################################
#
# mybatis mapper配置
#
############################################################
mapper:
  mappers: com.rookie.my.mapper.MyMapper
  not-empty: false
  identity: MYSQL
```

3. 引入MyMapper接口类

```java
package com.rookie.my.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 继承自己的MyMapper
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}

```

4. **使用逆向工具需要注意，重新生成的`xml`文件是一种追加的方式，每次生成完了最好去检查一下**

## 4. 基于通用Mapper基于Rest编写api接口

### 4.1 如果使用通用Mapper请注意：

1. **在启动类上加上如下注释**
2. 其中`@MapperScan(basePackages = "com.rookie.mapper")`来自 `import tk.mybatis.spring.annotation.MapperScan;`

```java
@SpringBootApplication
// 使用通用mapper的这个包就是tk开头的这个，需要指明这些包是在哪里
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.rookie.mapper")
public class RookieApplication {
    public static void main(String[] args) {
        SpringApplication.run(RookieApplication.class, args);
    }
}
```

3. 在service模块中添加相应逻辑，使用mapper操作数据库。注意，在Service的实现类上面要添加注解表明这是一个Bean，`@Service`
4. 在api模块中添加测试StuFooController，访问数据库中的stu表，查询数据。记得添加`@RestController`注解