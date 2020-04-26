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

# 1.1 实现单体电商项目核心功能

- 用户注册与登录
- Cookie 与 Session
- 集成 Swagger2 api
- 分类设计与实现
- 首页商品推荐
- 商品搜索与分页
- 商品详情与评论渲染
- 购物车与订单
- 微信与支付宝支付

## 1. 用户注册登录流程

### 1. 用户名注册

1. 首先创建一个`UserService`接口，用来定义用户相关的方法

```java
public interface UserService {
    // 用户注册的时候需要先去检查数据库中是否有这个用户存在了
    public boolean queryUsernameIsExist(String username);
    // UserBO 相当于是根据从前端取到的用户名称、密码构造一个新的用户
    public Users createUser(UserBO userBO);
}
```

2. 在`UserServiceImpl`中实现接口中的两个方法
   1. 其中查询是否存在的方法需要以`Propagation.SUPPORTS`的事务传播级别
   2. 创建用户的事务传播级别为`Propagation.REQUIRED`，创建失败需要回滚
3. 自定义响应数据结构 `RookieJsonResult`，前端接受此类数据（json object)后，可自行根据业务去实现相关功能
4. 其中为了设置全局唯一的用户ID，引入了`org.n3r.idworder`包
5. 为了设置时间，也引入了`DateUtil`类
6. 为了加密用户的密码，我们引入了`MD5Utils`
7. 引入性别枚举
8. 前端到后端的参数使用`UserBO`进行传递
9. 使用`postman`测试`api`层`PassportController`的功能是否正常

### 2. 邮箱注册

### 3. 手机号注册

## 2. 集成 Swagger2 API

> 为了减少程序员撰写文档的时间，使用`Swagger2`，只需要通过代码就能生成文档API提供给前端人员对接

```xml
<!-- swagger2 配置 -->
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger2</artifactId>
	<version>2.4.0</version>
</dependency>
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger-ui</artifactId>
	<version>2.4.0</version>
</dependency>
<dependency>
	<groupId>com.github.xiaoymin</groupId>
	<artifactId>swagger-bootstrap-ui</artifactId>
	<version>1.6</version>
</dependency>
```

1. 在`api`模块创建`config`包，创建`Swagger2.java`对象，做相关配置
2. 做好配置启动项目后，在网页中查看`http://localhost:8088/swagger-ui.html`，就能看到一份文档了。`http://localhost:8088/doc.html`也可以
3. 要想忽略某一个Controller，在他对应的类上方加上`@ApiIgnore`即可
4. 给Controller类加上接口注解，`@Api(value = "注册登录", tags = {"用户注册登录的相关接口"})`
5. 给类中某一个接口加上注解，`@ApiOperation(value = "判断用户是否存在", notes = "判断用户是否存在", httpMethod = "GET")` httpMethod要与下方请求方法相互匹配

![](https://yxin-images.oss-cn-shenzhen.aliyuncs.com/img/Snipaste_2020-04-08_20-32-23.jpg)

6. 对上面的请求参数，我们也可以加上注释，在`UserBO`类中。使用`@ApiModel`和`@ApiModelProperty`

```java
@ApiModel(value = "用户对象BO", description = "从客户端，由用户传入的数据封装在此entity中")
public class UserBO {
    @ApiModelProperty(value = "用户名", name = "username", example = "Allen", required = true)
    private String username;
    @ApiModelProperty(value = "密码", name = "password", example = "123456", required = true)
    private String password;
    @ApiModelProperty(value = "确认密码", name = "confirmPassword", example = "123456", required = true)
    private String confirmPassword;
```

## 3. 实现跨域配置，实现前后端联调配置

配置`config`目录下的`CorsConfig`文件，使得前后端联通

## 4. 用户登录功能

1. `service`层`UserService`中添加相应方法，然后到`Controller`中写一下用户`login`的`controller`
2. 登录后要隐藏掉用户的一些属性，使用`controller`中的`setNullProperty`方法实现
3. 引入`CookieUtils`和`JsonUtils`工具类，在`login`和`regist`中设置`cookie`，实现用户信息在页面显示

## 5. 聚合日志框架

### 1. 移除默认的日志

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <exclusions>
        <!--排除这个日志jar包-->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 2. 添加日志框架的依赖

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.21</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.21</version>
</dependency>
```

### 3. 创建 `log4j.properties` 并且放到资源文件目录下面 `src/main/resource` ，在 api 层

```properties
log4j.rootLogger=DEBUG,stdout,file
log4j.additivity.org.apache=true

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.threshold=INFO
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%-5p %c{1}:%L - %m%n

log4j.appender.file=org.apache.log4j.DailyRollingFileAppender
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.DatePattern='.'yyyy-MM-dd-HH-mm
log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
log4j.appender.file.Threshold=INFO
log4j.appender.file.append=true
log4j.appender.file.File=/workspaces/logs/rookie-api/rookie.log
```

### 4. 使用

```java
final static Logger logger = LoggerFactory.getLogger(HelloController.class);
```

### 5. 使用 AOP 进行日志打印

1. 先在 pom 中引入相应的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

2. 在 api 层中加入 aspect 包，编写切面逻辑

```java
/**
 * 切面表达式：
 * execution 代表所要执行的表达式主体
 * 第一处 * 代表方法返回类型 *代表所有类型
 * 第二处 包名代表aop监控的类所在的包
 * 第三处 .. 代表该包以及其子包下的所有类方法
 * 第四处 * 代表类名，*代表所有类
 * 第五处 *(..) *代表类中的方法名，(..)表示方法中的任何参数
 *
 */
@Around("execution(* com.rookie.service.impl..*.*(..))")
public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable{
    ...
}
```

## 6. 用户退出登录需要清除 cookie

使用到的接口就是 logout

```java
@ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
@PostMapping("/logout")
public RookieJsonResult logout(@RequestParam String userId,
                               HttpServletRequest request,
                               HttpServletResponse response) {
    // 使用 Cookie 工具进行清除                 Cookie名字
    CookieUtils.deleteCookie(request, response, "user");
    // TODO 生成用户token，存入redis会话
    // TODO 同步购物车数据  
    return RookieJsonResult.ok();
}
```

## 7. 开启 mybatis 日志 sql 打印

​	方便在开发测试的时候，及时通过打印出的 sql 语句来分析问题

```yml
# mybatis 的配置中添加
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

# 1.2 订单、支付、购物车等

## 1. 首页轮播图

### 1. IndexController

在第一个方法，**一定记得要返回 json 形式的对象给前端！！！！！！！**

```java
@ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
@GetMapping("/carousel")
// 返回值一定要使用 RookieJsonResult
public RookieJsonResult carousel() {

    List<Carousel> result = carouselService.queryAll(YesOrNo.YES.type);
    return RookieJsonResult.ok(result);
}
```

### 2. 加载与渲染大分类，实现一个懒加载的方式，只有当用户鼠标移过来才进行加载

​	商品左侧分类导航栏，实现一种鼠标划过来才进行加载的方式

### 3. 自定义 mapper 实现懒加载子分类展示

1. 通用 mapper 无法实现复杂的查询，自己编写 SQL 查询语句，根据父分类 id 查询子分类列表
2. 创建了新的自定义的 mapper 以及对应的 mapper.xml

## 2. 实现首页商品推荐

1. `ctrl alt o` 删除没有用到的包
2. 前后端在 `ItemInfoVO` 中对应的名字要相互对应上，不然前端就拿不到这个数据

```java
// 后端
public class ItemInfoVO {
    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;
}
// 前端 item.html 659 行，几个值要对应起来
	var item = itemInfo.item;
	var itemImgListTemp = itemInfo.itemImgList;
	var itemSpecListTemp = itemInfo.itemSpecList;
	this.itemParams = itemInfo.itemParams;
```

## 3. 实现商品评价

### 1. Spring Boot 整合 mybatis - pagehelper

1. 引入分页插件依赖

```xml
<!--pagehelper -->
<dependency>
	<groupId>com.github.pagehelper</groupId>
	<artifactId>pagehelper-spring-boot-starter</artifactId>
	<version>1.2.12</version>
</dependency>
```

2. 配置 yml

```yaml
# 分页插件配置
pagehelper:
  helperDialect: mysql
  supportMethodsArguments: true
```

3. 使用分页插件，在查询前使用分页插件，原理：统一拦截 sql ，为其提供分页功能

```java
// ItemServiceImpl中查询前使用的分页
/**
 * page: 第几页
 * pageSize: 每页显示条数
 */
PageHelper.startPage(page, pageSize);
```

4. 分页数据封装到 `PagedGridResult.java` 传给前端

```java
PageInfo<?> pageList = new PageInfo<>(list);
PagedGridResult grid = new PagedGridResult();
grid.setPage(page);
grid.setRows(list);
grid.setTotal(pageList.getPages());
grid.setRecords(pageList.getTotal());
```

### 2. 评价区域，对用户信息进行脱敏

引入工具类 `DesensitizationUtil.java`

```java
import sun.applet.Main;
/**
 * 通用脱敏工具类
 * 可用于：
 *      用户名
 *      手机号
 *      邮箱
 *      地址等
 */
public class DesensitizationUtil {

    private static final int SIZE = 6;
    private static final String SYMBOL = "*";

    public static void main(String[] args) {
        String name = commonDisplay("慕课网");
        String mobile = commonDisplay("13900000000");
        String mail = commonDisplay("admin@imooc.com");
        String address = commonDisplay("北京大运河东路888号");

        System.out.println(name);
        System.out.println(mobile);
        System.out.println(mail);
        System.out.println(address);
    }

    /**
     * 通用脱敏方法
     * @param value
     * @return
     */
    public static String commonDisplay(String value) {
        if (null == value || "".equals(value)) {
            return value;
        }
        int len = value.length();
        int pamaone = len / 2;
        int pamatwo = pamaone - 1;
        int pamathree = len % 2;
        StringBuilder stringBuilder = new StringBuilder();
        if (len <= 2) {
            if (pamathree == 1) {
                return SYMBOL;
            }
            stringBuilder.append(SYMBOL);
            stringBuilder.append(value.charAt(len - 1));
        } else {
            if (pamatwo <= 0) {
                stringBuilder.append(value.substring(0, 1));
                stringBuilder.append(SYMBOL);
                stringBuilder.append(value.substring(len - 1, len));

            } else if (pamatwo >= SIZE / 2 && SIZE + 1 != len) {
                int pamafive = (len - SIZE) / 2;
                stringBuilder.append(value.substring(0, pamafive));
                for (int i = 0; i < SIZE; i++) {
                    stringBuilder.append(SYMBOL);
                }
                if ((pamathree == 0 && SIZE / 2 == 0) || (pamathree != 0 && SIZE % 2 != 0)) {
                    stringBuilder.append(value.substring(len - pamafive, len));
                } else {
                    stringBuilder.append(value.substring(len - (pamafive + 1), len));
                }
            } else {
                int pamafour = len - 2;
                stringBuilder.append(value.substring(0, 1));
                for (int i = 0; i < pamafour; i++) {
                    stringBuilder.append(SYMBOL);
                }
                stringBuilder.append(value.substring(len - 1, len));
            }
        }
        return stringBuilder.toString();
    }

}
```

## 4. 实现商品搜索

1. 需要自定义相关的 sql 
2. 在搜索栏的搜索和在分类栏的搜索分开来进行，逻辑上是一样的

## 5. 实现收货地址相关

1. 收货地址的新增、修改、删除、设置默认等
2. 先从 `AddressService` 层开始设计，然后实现 `AddressServiceImpl`，再根据前端的接口，设计 `AddressController`

## 6. 确认订单（待发货部分应该有一个后台管理系统，由卖家发货，暂时没有，后面可以以一个分支的方式补上来）

> **流程:** 用户 ——> 选择商品——>加入购物车——>订单结算——>选择支付方式——>支付

![](https://yxin-images.oss-cn-shenzhen.aliyuncs.com/img/Snipaste_2020-04-24_22-52-32.jpg)

![](https://yxin-images.oss-cn-shenzhen.aliyuncs.com/img/Snipaste_2020-04-24_22-53-11.jpg)

![](https://yxin-images.oss-cn-shenzhen.aliyuncs.com/img/Snipaste_2020-04-24_22-53-21.jpg)

## 7. 创建订单

1. 创建订单过程中的减库存操作可能会造成超卖，先使用数据库乐观锁来实现，后期**使用分布式锁（zookeeper redis）**

## 8. 微信支付

1. 账号和密码是在 `github` 上面找的
2. 需要同步更新到前端代码的 `wxpay.html` 的189行

```java
headers.add("imoocUserId", "6567325-1528023922");
headers.add("password", "342r-t450-gr4r-456y");
```

3. 要使用到内网穿透，以便使得本地的订单状态在支付之后能够和支付中心的一致

4. 使用到的工具就是 `natapp`（注意这个工具开启之后，免费的可能公网地址会变，所以不得行的时候就去修改 `BaseController` 中的 `payReturnUrl` ）

5. 这一部分的重点在于 `OrderServiceImpl.java` 中的 `createOrder()` 方法，其中包含了很多步骤
6. 在 `OrdersController` 中调用了创建订单的方法

## 9. 支付宝支付

1. 同样需要在前端代码中修改 `alipayTempTransit.html` 118 119 行的账号和密码，同上

## 10. 定时任务

1. `@Scheduled(default = " ")`  中间的字符串在这个网站可以在线生成 `https://cron.qqe2.com/`

2. 启动类开启 `@EnableScheduling       // 开启定时任务`
3. 定时任务有弊端

```java
/**
 * 使用定时任务关闭超期未支付订单，会存在的弊端：
 * 1. 会有时间差，程序不严谨
 *      10:39下单，11:00检查不足1小时，12:00检查，超过1小时多余21分钟
 * 2. 不支持集群
 *      单机没毛病，使用集群后，就会有多个定时任务
 *      解决方案：只使用一台计算机节点，单独用来运行所有的定时任务
 * 3. 会对数据库全表搜索，及其影响数据库性能：select * from order where orderStatus = 10;
 * 定时任务，仅仅只适用于小型轻量级项目，传统项目
 *
 * 后续会涉及到消息队列：MQ-> RabbitMQ, RocketMQ, Kafka, ZeroMQ...
 *      延时任务（队列）
 *      10:12分下单的，未付款（10）状态，11:12分检查，如果当前状态还是10，则直接关闭订单即可
 */
```

