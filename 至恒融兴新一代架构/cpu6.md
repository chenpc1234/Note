# SpringBoot微服务开发

[springboot]: https://github.com/chenpc1234/Note/blob/main/SpingBoot/SpringBoot.md



# 注册中心Eureka

[EUREKA 注册中心]: https://github.com/chenpc1234/Note/blob/main/SpringCloud/1.%20%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%8E%E5%8F%91%E7%8E%B0/%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%8E%E5%8F%91%E7%8E%B0.md

# 服务调用-Openfeign

[Note/服务调用.md at main · chenpc1234/Note · GitHub](https://github.com/chenpc1234/Note/blob/main/SpringCloud/2. 服务调用/服务调用.md)

# 网关zuul

[Zuul]: https://github.com/chenpc1234/Note/blob/main/SpringCloud/4.%20%E6%9C%8D%E5%8A%A1%E7%BD%91%E5%85%B3/Zuul/Zuul.md



## 身份验证

- 验证码
  - 问题： 验证码保存在数据库CUP_ORG_CHECK_CODE   --》并发量高引起服务器崩溃
  - 180s 有效。

- 用户名密码
  
- 密码前台加密 
  
- JWT令牌

  - [JWT令牌]: https://github.com/chenpc1234/Note/blob/main/Spring/SpringSecurity/%E5%8D%95%E7%82%B9%E7%99%BB%E5%BD%95/JWT%E4%BB%A4%E7%89%8C.md

  - 问题： 后台修改服务器密码，用户jwt仍然有效。--》 签名加密 盐值 使用固定秘钥+用户密码

### 过滤器

1. 白名单：UrlLimitFilter

2. JWT认证： TokenFilter

3. 鉴权：CupApiFilter

   ​	问题 :  网关过滤器 承载了全部请求-不应该出线大规模查询数据库的行为

