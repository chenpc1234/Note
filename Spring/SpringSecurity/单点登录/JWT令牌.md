# JWT

​		JWT用于分布式系统的单点登录SSO场景，主要用来做用户身份鉴别或者资源接口安全性的技术。

## token进行用户身份验证

### 流程

1. 客户端使用用户名和密码请求登录
2. 服务端收到请求，验证用户名和密码
3. 验证成功后，服务端会签发一个token，再把这个token返回给客户端
4. 客户端收到token后可以把它存储起来，比如放到cookie中
5. 客户端每次向服务端请求资源时需要携带服务端签发的token，可以在cookie或者header中携带(header中更好)
6. 服务端收到请求，然后去验证客户端请求里面带着的token，如果验证成功，就向客户端返回请求数据

### 优点

1. 支持跨域访问：

   - cookie是无法跨域的，而token由于没有用到cookie(前提是将token放到请求头中)，所以跨域后不会存在信息丢失问题

     - cookie可以放大作用域来解决跨域(实现域名放大效果，所有子域名下Session共享)

       ```java
       /**
            * 定义序列化器的 Cookie名称、作用域(实现域名放大效果，所有子域名下Session共享)
            */
       @Bean
       public CookieSerializer cookieSerializer() {
           DefaultCookieSerializer serializer = new DefaultCookieSerializer();
           serializer.setCookieName("CHEN_SESSION_ID");
           serializer.setDomainName("chenpc.com");
           serializer.setSameSite(null);
           return serializer;
       }
       ```

2. 无状态：

   - token机制在服务端不需要存储session信息，因为token自身包含了所有登录用户的信息，所以可以减轻服务端压力

3. 更适用CDN

   - 可以通过内容分发网络请求服务端的所有资料

4. 更适用于移动端：

   - 当客户端是非浏览器平台时，cookie是不被支持的，此时采用token认证方式会简单很多

5. 无需考虑CSRF

   - 由于不再依赖cookie，所以采用token认证方式不会发生CSRF，所以也就无需考虑CSRF的防御



## Json Web Token认证流程

​		**jwt=安全的token。**JWT的本质就是一个字符串，它是将用户信息保存到一个Json字符串中，然后进行编码后得到一个`JWT token`，并且这个`JWT token`带有签名信息，接收后可以校验是否被篡改，所以可以用于在各方之间安全地将信息作为Json对象传输。

![img](JWT令牌.assets/900b3e81f832b2f08c2e8aabb540536a.png)

1. 前端通过Web表单将自己的用户名和密码发送到后端的接口

2. 后端核对用户名和密码成功后，将包含用户信息的数据作为JWT的Payload，将其与JWT Header分别进行Base64编码拼接后签名，形成一个JWT Token，形成的JWT Token就是一个字符串： aaa.bbb.ccc

   ```tex
   token: "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJicmFuY2hJZCI6IlNSQ0IwMDAwMDAwMDAwMDEiLCJyb2xlSWQiOiJTUkNCMDAwMDAwMDAwMDAxIiwidG9rZW5VdWlkIjoiNDE0MzhlNzYtMjFkNS00MmUxLTljY2MtMmYwZDMwYzIzNDAwIiwiZXhwIjoxNjU4MTM3NTcxLCJ1c2VySWQiOiJTMDAwMDAwMDAwMDAwMDAxIn0.FyQGGFox3DlMryEVi7RA_wtz4bU9UT-DnJCcry7jNCY"
   ```

3. 后端将JWT Token字符串作为登录成功的结果返回给前端。前端可以将返回的结果保存在浏览器中，退出登录时删除保存的JWT Token即可

4. 前端在每次请求时将JWT Token放入HTTP请求头中的Authorization属性中(解决XSS和XSRF问题)

5. 后端检查前端传过来的JWT Token，验证其有效性，比如检查签名是否正确、是否过期、token的接收方是否是自己等等。验证通过后，后端解析出JWT Token中包含的用户信息，进行其他逻辑操作(一般是根据用户信息得到权限等)，返回结果
   


## JWT结构

​		JWT由3部分组成：标头(Header)、有效载荷(Payload)和签名(Signature)。在传输的时候，会将JWT的3部分分别进行Base64编码后用.进行连接形成最终传输的字符串
$$
JWTString=Base64(Header).Base64(Payload).HMACSHA256(base64UrlEncode(header)+"."+base64UrlEncode(payload)+"."+secret)
$$

### 1.Header

​		JWT头是一个描述JWT元数据的JSON对象，**alg属性表示签名使用的算法**，默认为HMAC SHA256（写为HS256）；typ属性表示令牌的类型，JWT令牌统一写为JWT。最后，使用Base64 URL算法将上述JSON对象转换为字符串保存

```json
{"typ":"JWT","alg":"HS256"}
```

### 2.Payload

​		有效载荷部分，是JWT的主体内容部分，也是一个JSON对象，包含需要传递的数据。 JWT指定七个默认字段供选择

```tex
iss：发行人
exp：到期时间
sub：主题
aud：用户
nbf：在此之前不可用
iat：发布时间
jti：JWT ID用于标识该JWT
```


​		这些预定义的字段并不要求强制使用。除以上默认字段外，我们还可以自定义私有字段，一般会把包含用户信息的数据放到payload中，如下例：

```json
{"branchId":"SRCB000000000001","roleId":"SRCB000000000001","tokenUuid":"41438e76-21d5-42e1-9ccc-2f0d30c23400","exp":1658137571,"userId":"S000000000000001"}
```


​		请注意，默认情况下JWT是未加密的，因为只是采用base64算法，拿到JWT字符串后可以转换回原本的JSON数据，任何人都可以解读其内容，因此不要构建隐私信息字段，比如用户的密码一定不能保存到JWT中，以防止信息泄露。JWT只是适合在网络中传输一些非敏感的信息

### 3.Signature

签名哈希部分是对上面两部分数据签名，需要使用base64编码后的header和payload数据，通过指定的算法生成哈希，以确保数据不会被篡改。首先，需要指定一个密钥（secret）。该密码仅仅为保存在服务器中，并且不能向用户公开。然后，使用header中指定的签名算法（默认情况下为HMAC SHA256）根据以下公式生成签名
$$
HMACSHA256(base64UrlEncode(header)+"."+base64UrlEncode(payload),secret)
$$
在计算出签名哈希后，JWT头，有效载荷和签名哈希的三个部分组合成一个字符串，每个部分用.分隔，就构成整个JWT对象

![img](JWT令牌.assets/a457f8856a8f8288702ab56b18ed4818.png)

### 服务端验证

在接收到客户端发送过来的JWT token之后：

1. header和payload可以直接利用base64解码出原文，从header中获取哈希签名的算法，从payload中获取有效数据
2. signature由于使用了不可逆的加密算法，无法解码出原文，它的作用是校验token有没有被篡改。
   1. 服务端获取header中的加密算法之后，利用该算法加上secretKey对header、payload进行加密，比对加密后的数据和客户端发送过来的是否一致。
   2. 注意secretKey只能保存在服务端，而且对于不同的加密算法其含义有所不同，一般对于MD5类型的摘要加密算法，secretKey实际上代表的是盐值
      

## java使用jwt

### 添加依赖

```xml
<!--JWT -->
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
</dependency>
```

### 相关代码

```java
//获取token
public static String getToken(String userId, String roleId, String branchId, String tokenUUID)
			throws Exception {
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.MINUTE, timeToIdleSeconds);
		// 创建JWT builder
		JWTCreator.Builder builder = JWT.create();
		// payload
		builder.withClaim(USER_ID, userId);
		builder.withClaim(ROLE_ID, roleId);
		builder.withClaim(BRANCH_ID, branchId);
		builder.withClaim(BRANCH_ID, branchId);
		if(StringUtil.isEmpty(tokenUUID)) {
			builder.withClaim(TOKEN_UUID, UUID.randomUUID().toString());
		}else {
			builder.withClaim(TOKEN_UUID, tokenUUID);
		}
		// 指定令牌的过期时间
		String token = builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(secret));// 签名
		return token;
	}

/**
	 * 验证token合法性，获取信息
	 */
	public static DecodedJWT verify(String token) {
		DecodedJWT verify = null;
		if (StringUtils.isEmpty(token)) {
			throw new CupException("token不能为空");
		}
		try {
			// 校验token
			verify = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
		} catch (SignatureVerificationException e) {
			throw new CupException("签名无效");
		} catch (TokenExpiredException e) {
			throw new CupException("用户登录失效,请重新登录!");
		} catch (AlgorithmMismatchException e) {
			throw new CupException("token算法不一致");
		} catch (Exception e) {
			throw new CupException("token无效");
		}
		return verify;
	}
```

## 为什么要用JWT

### 传统Session认证的弊端

​			我们知道HTTP本身是一种无状态的协议，这就意味着如果用户向我们的应用提供了用户名和密码来进行用户认证，认证通过后HTTP协议不会记录下认证后的状态，那么下一次请求时，用户还要再一次进行认证，因为根据HTTP协议，我们并不知道是哪个用户发出的请求，所以为了让我们的应用能识别是哪个用户发出的请求，我们只能在用户首次登录成功后，在服务器存储一份用户登录的信息，这份登录信息会在响应时传递给浏览器，告诉其保存为cookie，以便下次请求时发送给我们的应用，这样我们的应用就能识别请求来自哪个用户了，这是传统的基于session认证的过程


![img](JWT令牌.assets/29cfe2cc7bd13bc659227e62c3e89063.png)

然而，传统的session认证有如下的问题：

- 每个用户的登录信息都会保存到服务器的session中，随着用户的增多，服务器开销会明显增大
- 由于session是存在与服务器的物理内存中，所以在分布式系统中，这种方式将会失效。虽然可以将session统一保存到Redis中，但是这样做无疑增加了系统的复杂性，对于不需要redis的应用也会白白多引入一个缓存中间件
- 对于非浏览器的客户端、手机移动端等不适用，因为session依赖于cookie，而移动端经常没有cookie
- 因为session认证本质基于cookie，所以如果cookie被截获，用户很容易收到跨站请求伪造攻击。并且如果浏览器禁用了cookie，这种方式也会失效
- 前后端分离系统中更加不适用，后端部署复杂，前端发送的请求往往经过多个中间件到达后端，cookie中关于session的信息会转发多次
- 由于基于Cookie，而cookie无法跨域，所以session的认证也无法跨域，对单点登录不适用

### JWT认证的优势

- JWT Token数据量小，传输速度也很快
- 因为JWT Token是以JSON加密形式保存在客户端的，所以JWT是跨语言的，原则上任何web形式都支持
- 不需要在服务端保存会话信息，也就是说不依赖于cookie和session，所以没有了传统session认证的弊端，特别适用于分布式微服
- 单点登录友好：使用Session进行身份认证的话，由于cookie无法跨域，难以实现单点登录。但是，使用token进行认证的话， token可以被保存在客户端的任意位置的内存中，不一定是cookie，所以不依赖cookie，不会存在这些问题
- 适合移动端应用：使用Session进行身份认证的话，需要保存一份信息在服务器端，而且这种方式会依赖到Cookie（需要 Cookie 保存 SessionId），所以不适合移动端

## 问题

### 注销登录等场景下 JWT 还有效

这个问题不存在于 Session 认证方式中，因为在 Session 认证方式中，遇到这种情况的话服务端删除对应的 Session 记录即可。但是，使用 JWT 认证的方式就不好解决了。我们也说过了，**JWT 一旦派发出去，如果后端不增加其他逻辑的话，它在失效之前都是有效的。**

- 退出登录;  前台- > remove token

- 修改密码;  前台- > remove token    

- 服务端修改了某个用户具有的权限或者角色； jwt 黑名单 

- 用户的帐户被封禁/删除； jwt 黑名单

- 用户被服务端强制注销； jwt 黑名单

- 用户被踢下线； jwt 黑名单

  

### JWT 的续签问题

我们先来看看在 Session 认证中一般的做法：假如 Session 的有效期 30 分钟，如果 30 分钟内用户有访问，就把 Session 有效期延长 30 分钟。

**方案**

- 每一次请求都进行重新生成一个新的token【频率过高，性能不好】
- 每次登录的时候生成两个token给前端进行返回，一个是用于鉴别用户身份的token，另外一个token则是用于刷新token用的
- 登录过后给前端进行返回token并设置了过期时间30分钟，每次请求的时候前端把token存在请求头里面进行发请求，后端接收请求的时候获取请求头出来进行jwt解析判断过期时间是否小于10分钟，如果小于10分钟就生成新的token在responseHearde进行返回即可
  