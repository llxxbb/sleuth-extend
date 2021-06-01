# 开发过程中的问题

## p1:

2020-08-12 14:35:58,500 WARN - org.springframework.context.annotation.ConfigurationClassPostProcessor[373] - [main] - Cannot enhance @Configuration bean definition 'propertySourcesPlaceholderConfigurer' since its singleton instance has been created too early. The typical cause is a non-static @Bean method with a BeanDefinitionRegistryPostProcessor return type: Consider declaring such methods as 'static'. 

解决方法:

https://github.com/ctripcorp/apollo/issues/1156

## 自定义采样率

[sleuth+zipkin自定义采样率（九） | sharedCode (shared-code.com)](https://shared-code.com/article/109)



