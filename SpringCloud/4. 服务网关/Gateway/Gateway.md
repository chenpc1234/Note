# Gateway

##  Spring cloud Gateway特性

1. 基于Spring Framework 5，Project  Reactor和Spring Boot 2.0。
2. 集成Hystrix断路器。
3. 集成Spring Cloud DiscoveryClient。
4. Predicates和Filters作用于特定路由，易于编写的Predicates和Filters。
5. 具备一些网关的高级功能：动态路由、限流、路径重写
  6.Webflux中的reactor-netty响应式编程组件，底层使用了Netty通讯框架。

从以上的特征来说，和Zuul的特征区别不大。SpringCloud Gateway和Zuul主要的区别，还是在底层的通信框架上。

## 三大核心概念

- Route(路由)：
  - 路由是构建网关的基本模块，它由ID，目标URI，一系列的断言和过滤器组成，如果断言为true则匹配该路由，目标URI会被访问。
- Predicate(断言)：
  - 这是一个java 8的Predicate，可以使用它来匹配来自HTTP请求的任何内容，如：请求头和请求参数。断言的输入类型是一个ServerWebExchange。
- Filter(过滤器)：
  - 指的是Spring框架中GatewayFilter的实例，使用过滤器，可以在请求被路由前或者后对请求进行修改。

总结：web请求，通过一些匹配条件，定位到真正的服务节点。并在这个转发过程的前后，进行一些精细化控制。predicate就是匹配条件，而filter，就可以理解为一个无所不能的拦截器。有了这两个元素，再加上目标URI，就可以实现具体的路由了。

