# SpringSecurity

## 简介



## 用户认证

### 用户名密码配置

#### 方式一：配置文件

```properties
spring.security.user.name=chen
spring.security.user.password=11111111
```

#### 方式二：配置类

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode("123");
        auth.inMemoryAuthentication().withUser("chen").password(password).roles("admin");
    }
    @Bean
    PasswordEncoder password(){
        return new BCryptPasswordEncoder();
    }
}
```

#### 方式三：自定义编写实现类



## BCryptPasswordEncoder

密码保存应该使用加密的方式，加密方式

1. 使用MD5加密 (压缩性、容易计算、抗修改、强碰撞 ) --> MD5 虽然是不可逆算法，但是网络上通过枚举已经破译了绝大部分

2. 使用MD5 + 随机盐值 -->   数据库两个字段  col1=加密密码 col2=盐值
	前台传入用户名密码 ->  根据用户名得到盐值 ->（密码+ 盐值）进行MD5加密----> 加密密码比较

3. BCryptPasswordEncoder  SHA-256 +随机盐+密钥对密码进行加密

   1. 加密过程

      1. 生成随机盐
      2. 对(密码+盐值)进行hash加密，得到密文
      3. 特殊字段+盐值+密文 = 加密密码，进行返回

   2. 比较过程

      1. 传入密码  ，加密密码。 通过加密密码得到（密文、盐值）
      2. 将（传入密码+盐值）进行加密后-->新密文
      3. 比较密文得到结果，进行返回

   3. ```java
      BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
      String "加密密码" = passwordEncoder.encode( "输入密码"); 
      boolean flag = passwordEncoder.matches("输入密码", "加密密码");
      ```



## Oauth2 社交登陆

**微博社交登陆案例**

  	参照微博开放文档：  https://open.weibo.com/apps/3751148615/privilege

1. 引导用户到微博的登陆验证界面，微博登陆成功后跳转到处理界面
   https://api.weibo.com/oauth2/authorize?client_id=3751148615&response_type=code&redirect_uri=http://auth.gulimall.com/oauth2.0/weibo/success

   1. client_id  是自己应用的AppKey
   2. redirect_uri  是自己的授权回调页

2. 登陆成功后微博会跳转到指定的回调页，并携带返回code。

3. 编写业务处理接口auth.gulimall.com/oauth2.0/weibo/success，用code 换取用户的 access_token，发送post请求携带必要数据

   ```java
   @RequestMapping("/oauth2.0/weibo/success")
   public String authorize(String code, HttpSession session) throws Exception {
       //1. 使用code换取token，换取成功则继续2，否则重定向至登录页
       Map<String, String> query = new HashMap<>();
       query.put("client_id", "3751148615");
       query.put("client_secret", "25686d059eccc582d779cb9ef86bf421");
       query.put("grant_type", "authorization_code");
       query.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
       query.put("code", code);
       //发送post请求换取token
       HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<String, String>(), query, new HashMap<String, String>());
       Map<String, String> errors = new HashMap<>();
       if (response.getStatusLine().getStatusCode() == 200) {
           //2. 调用member远程接口进行oauth登录，登录成功则转发至首页并携带返回用户信息，否则转发至登录页
           String json = EntityUtils.toString(response.getEntity());
           SocialUser socialUser = JSON.parseObject(json, new TypeReference<SocialUser>() {
           });
           R login = memberFeignService.login(socialUser);
           //2.1 远程调用成功，返回首页并携带用户信息
           if (login.getCode() == 0) {
               String jsonString = JSON.toJSONString(login.get("memberEntity"));
               System.out.println("----------------"+jsonString);
               MemberResponseVo memberResponseVo = JSON.parseObject(jsonString, new TypeReference<MemberResponseVo>() {
               });
               System.out.println("----------------"+memberResponseVo);
               session.setAttribute(AuthServerConstant.LOGIN_USER, memberResponseVo);
               return "redirect:http://gulimall.com";
           }else {
               //2.2 否则返回登录页
               errors.put("msg", "登录失败，请重试");
               session.setAttribute("errors", errors);
               return "redirect:http://auth.gulimall.com/login.html";
           }
       }else {
           errors.put("msg", "获得第三方授权失败，请重试");
           session.setAttribute("errors", errors);
           return "redirect:http://auth.gulimall.com/login.html";
       }
   ```

4. 使用token 获取用户信息

   ```java
   Map<String, String> query = new HashMap<>(2);
   query.put("access_token",？？？？);
   query.put("uid", uid);
   HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(0), query);
   ```

   