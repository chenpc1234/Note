# SpringSecurity

# 简介



# 用户认证

## 用户名密码配置

### 方式一：配置文件

```properties
spring.security.user.name=chen
spring.security.user.password=11111111
```

### 方式二：配置类

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

### 方式三：自定义编写实现类

