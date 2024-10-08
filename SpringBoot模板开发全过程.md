# **企业开发流程：**

需求分析 => 设计（概要设计、详细设计）=> 技术选型 =>初始化 / 引入需要的技术 => 写 Demo => 写代码（实现业务逻辑） => 测试（单元测试）=> 代码提交 / 代码评审 => 部署=> 发布

# 需求分析

制作一个SpringBoot单体架构通用模板。为了方面后续项目快速初始化。

功能模块：

1. 生成验证码	
2. 注册、登录
3. 分页、查询、修改、批量删除、添加用户

# 技术选型

JDK1.8 ✔

SpringBoot 2.7.2 ✔

MyBatis Plus ✔

MySQL 数据库 ✔

Apache Commons Lang3 工具类 ✔

GSON JSON工具 ✔

Lombok 注解 ✔

Hutool 工具库 ✔

Swagger + Knife4j 接口文档 ✔

Redis 数据库 ✔

Minio 分布式存储

Easy Excel 表格处理

# 系统设计

## 1. 生成验证码 ✔

设计通用一些，有可能不止登录使用。设计成一个生成验证码的工具类

**流程**

登录界面一加载出来，就请求生成验证码的接口。

后端验证码生成之后，将值保存到redis中/session中，设计过期时间60s。

key：startup:login:captcha: id:uuid	value：text

将生成的验证码图像，uuid返回给前端。

**接口设计**

请求地址：get	/login/captcha 

参数：无

返回值：base64加密字符串、uuid

**问题**

1. 直接将验证码存到session中，redis中只存一个验证码没必要。
   - 如果程序要分布式部署，那么就需要使用redis，但是也可以设计分布式session。同样存session的代码无需修改，分布式session会自动同步到redis中。
2. 如果用户无限请求生成验证码的接口，如何限制？
3. idea如何忽略git文件 ✔

~~~java
git rm --cache -rf [文件名/文件夹名]
~~~

**优化**

1. 设计一个缓存类，分别定义缓存session和redis的方法。

## 2. 用户注册、登录✔

>不要过度设计、开闭原则

**库表设计** 

user表	用户表

user_id	bigint	pk	用户id

user_account	varchar(30)	NOT NULL	登录账号	唯一索引

user_password	varchar(512)	NOT NULL	登录密码

username	varchar(50)	NOT NULL	姓名

gender	tinyint	DEFAULT 2	0 - 男	1 - 女	2 - 未知

phone	varchar(128)	手机

avatar_url	varchar(1024)	头像

user_status	tinyint	NOT NULL	DEFAULT 0	0 - 正常	1 - 禁用

create_time	datetime	DEFAULT	CURRENT_TIMESTAMP	创建时间

update_time	datetime	DEFAULT	CURRENT_TIMESTAMP	根据当前时间戳更新	更新时间

is_delete	tinyint	NOT NULL	DEFAULT 0	0 - 未删除	1 - 已删除	是否删除

**注册接口设计**

请求地址：post	/user/register

参数：UserRegisterDTO

返回值：无

**注册流程**

1. 参数格式校验
   1. String去空格
   2. user_account不为null	 >= 4位 && <=30位	
   3. user_password不为null    >= 6位
   4. rePassword不为null  && 与password相同
2. 校验account是否已存在
   1. 已存在：直接返回失败
   2. 不存在：数据库添加用户

**登录接口设计**

请求地址：post	/user/login

参数：UserLoginDTO

返回值：UserVO

**登录流程**

1. 参数格式校验
   1. String去空格
   2. account不为null
   3. password不为null  
   4. captcha不为null     
      1. 根据captchaId查询redis
         1. 不存在，返回失败
         2. 存在，验证text与redis中是否一致。不一致：失败。
   5. captchaId不为null
2. 校验account数据库是否已存在
   1. 不存在：账号或密码错误
   2. 存在。
      1. 账户是否被禁用，禁用：返回账户已被禁用
      2. 未禁用：登录成功。保存用户登录态(Session)，清除验证码缓存，返回UserVO给前端。

**注意**

与数据库打交道的语句尽量放到后面

登录验证成功之后保存登录态

**优化**

1. 设计登录拦截器，拦截需要登录的请求。

## 3. 用户条件分页查询 ✔

**接口设计**

请求地址：get	/user/page

参数：user_account、username、gender、phone、pageSize、pageNum

返回值：PageVO

**流程**

1. 验证参数
2. 根据条件，拼接sql语句
3. 返回封装的PageVO

**问题**

1. 是否限制要pageSize、pageNum的大小？如果数据库数据特别多，pageNum特别大时查询会慢。

**优化**

可以将分页的返回结果进行PageVO封装，从而进行脱敏。

## 4. 增 删 改用户 ✔

**增流程**

管理员可以在后台添加用户。

要求必须输入user_account、user_password、user_role

流程与注册类似

**增接口**

请求地址：post	/user/addUser

请求参数：AddUserDTO

返回值：null



**删流程**

管理员可以在后台删除用户。

根据userId进行删除，支持批量删除

**删接口**

请求地址：get 	/user/batch_delete

请求参数：List<Long>

返回值：null



**改流程**

注意：是忽略null、还是修改null

选择修改null

1. 管理员可以修改任何用户的信息。

   - 可修改内容：user_account、username、gender、phone、user_role、user_status
   - 请求路径：post    /user/update_admin
   - 参数：UserUpdateDTO
   - 返回：null

   流程：

   1. 校验参数、account长度、不为空
   2. 判断修改后account是否存在（不包括被修改的userId）    存在：账号已存在
   3. 根据userId查询待修改的用户信息。将信息修改

2. 用户只可以修改自己的信息。

   - 可修改内容：user_account、username、gender、phone、avatar_url
   - 请求路径：post    /user/update
   - 参数：UserUpdateDTO
   - 返回：null

   流程：

   1. 验证参数、account长度、不为空
   2. 判断修改的用户id是否是当前自己登录的id    不是：返回修改失败
   3. 判断修改后account是否存在（不包括自己）    存在：账号已存在
   4. 根据userId查询待修改的用户信息。将信息修改



## 5. 用户退出 ✔

**流程**

首先根据userId，判断退出的用户是否是自己登录的

是：清除session

不是：返回异常

**接口**

请求地址：get    /user/logout

参数：userId

返回：null

## 坑

1. SpringBoot 2.7.x 与 knife4j-spring-boot-starter 3.x 会报错：`Failed to start bean ‘documentationPluginsBootstrapper‘； nested exception is java.lang.NullPoint`

解决方案：

​	yml配置修改

~~~yml
spring:
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
~~~
