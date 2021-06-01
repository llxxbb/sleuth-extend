# Sleuth Extend

本扩展主要增强 [spring cloud sleuth](https://docs.spring.io/spring-cloud-sleuth/docs/3.0.x/reference/htmlsingle) 的功能，用于解决 sleuth 在数据采集方面不方便的问题，如下：

- 解决不能对数据自动序列化的问题，此项目使用 jackson 对数据自动序列化。
- 方便代码中嵌入跟踪信息。
- 解决函数返回值不方便采集的问题。
- 解决不方便对参数属性进行采集的问题。

## 使用方法

### pom.xml

```xml
<dependency>
    <groupId>io.github.llxxbb</groupId>
    <artifactId>sleuth-extend</artifactId>
    <version>1.0.0</version>
</dependency>
```

为了避免对使用方spring boot 版本造成冲突，使用方需额外引入下面的包

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```

如果使用 kafka 来发送采集数据请引入 kafka:

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

### 进行 application.yml 配置

设置项目名，用于标记链路跟踪来源于那个项目，请替换掉 [your_app_name]

```yaml
spring.application.name: your_app_name
```

过滤掉不需要跟踪的 url， **注意**：形式为正则表达式，下面用于过滤项目的存活监控页 /monitorDB/monitor.shtml

```yaml
spring.sleuth.web.additional-skip-pattern: ".*/monitor\\.shtml"
```

异步 http 发送链路跟踪信息

```yaml
spring.zipkin.sender.type: web
spring.zipkin.baseUrl: http://[zipkin-server]/
```

kafka 发送链路跟踪信息， 注意：目前使用的是 kafka2

```yaml
spring.zipkin.sender.type: kafka
spring.kafka.bootstrap-servers: [zkserver01:9092,zkserver02:9092，zkserver03：9092]
```

## @TracePara

此注解在链路追踪中缺省捕获所有的入参和出参。

```java
@PostMapping("/addUser")
@TracePara(append = {"user.name"})
UserInfo addUser(@RequestBody UserInfo user) {
    // Your logical go here
    // ...
    return user;
}
```

`@TracePara`注解有下面的属性

- value：为跟踪的名称，如不指定则使用方法名。如本例为: add-user，**注意**驼峰转换成了"-"格式。
- append：为额外要添加跟踪的入参数据。这些数据会以 tag 的形式体现，tag 的 key 为 path,，tag 的 value 为对应的入参数据。可以指定多个，如找不到对应的数据则自动忽略。如此例
  - user.name：key 为 user.name， value 为 user 参数中的 name 属性值。
- resultTag：为函数的返回值添加 tag, 缺省为 result。
- exceptionTag：如果函数运行过程中抛出 `Throwable` 则将异常信息放到此 tag 中。此值缺省为 error。

### @SpanExtend

此注解缺省情况下不捕获入参和出参。

```java
@PostMapping("/addUser")
@SpanExtend(resultTag = "result", path = {"user.name", "user"})
UserInfo addUser(@RequestBody UserInfo user) {
    // Your logical go here
    // ...
    return user;
}
```

`@SpanExtend` 注解有下面的属性

- value：为跟踪的名称，如不指定则使用方法名。如本例为: add-user，**注意**驼峰转换成了"-"格式。
- path：指定要添加跟踪的入参数据。这些数据会以 tag 的形式体现，tag 的 key 为 path,，tag 的 value 为对应的入参数据。可以指定多个，如找不到对应的数据则自动忽略。此例加了两个
  - user.name：key 为 user.name， value 为 user 参数中的 name 属性值。
  - user：key 为 user, value 为 user 参数。
- resultTag：为函数的返回值添加 tag, 如不指定则不对返回值进行跟踪。
- exceptionTag：如果函数运行过程中抛出 `Throwable` 则将异常信息放到此 tag 中。此值缺省为 error。

### TracerExtend

如果你在**逻辑处理过程中**需要添加跟踪信息，则需要使用 `TracerExtend` ，它会在当前线程的跟踪信息中添加 tag， 如果当前线程中不存在跟踪信息则忽略此请求。使用方式示例如下：

```java
@RestController
public class UserService {
    private final TracerExtend extend;

    public UserService(TracerExtend extend) {
        this.extend = extend;
    }

	@PostMapping("/addUser")
	UserInfo addUser(@RequestBody UserInfo user) {
	    // ...
        // add trace info, tag is "userId", value is [user.id]
        extend.addTag("userId", user.id);
	    // ...
	    return user;
	}
}
```

## Release
1.0.0 2021-06-01

