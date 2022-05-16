# Redisson

## Redisson的诞生

Redisson分布式锁是基于Redis 设计，缓存三大问题解决及发展如下：

1. 缓存穿透。 查询一个一定不存在的数据，就会去访问数据库

   →缓存null值，并设置过期时间

2. 缓存雪崩。 大量的key 在同一个时间过期，过期后遇到高并发查询，引起缓存雪崩

   →缓存时间加随机值（其实没啥用）

3. 缓存击穿。 一个热点key 在某个时刻过期，然后突然遇到高并发访问这个key，会引起缓存击穿。

   →访问数据库时加锁

   1. →本地锁synchronize和 lock →分布式不适用
   2. →分布式锁--redis 
      1. 占据锁 使用setNx  设置锁过期时间 30s
      2. 删除锁
         1. 保证删除的是自己的锁，不然锁过期了，其他线程拿到锁，可能删除的是其他线程的锁
            - →value值设置为随机值，删除的时候进行比较然后删除
         2. 业务处理时间超过锁时间
            1. →设置锁过期时间时设置的大一些，多少合适？
            2. →锁续期  →看门狗机制→Redisson

## 微服务整合Redisson

1. 微服务引入Redisson依赖

   ```xml
   <!-- 引入redisson 作为分布式锁 -->
   <dependency>
       <groupId>org.redisson</groupId>
       <artifactId>redisson</artifactId>
       <version>3.12.0</version>
   </dependency>
   ```

2. 微服务添加Redisson配置

   ```java
   @Configuration
   public class MyRedissonConfig {
   
       @Bean(destroyMethod = "shutdown")
       public RedissonClient redisson() {
           Config config = new Config();
           /*config.useClusterServers()
                .addNodeAddress("127.0.0.1:7181");*/
           config.useSingleServer().setAddress("redis://glsc:6379");
           return Redisson.create(config);
       }
   }
   ```

3. Redisson分布式锁使用，与JUC的lock锁大体一致

   ```java
   @Autowired
   private RedissonClient redissonClient;
   
   private Map<String, List<Catalog2Vo>> getCatelogJsonWithRedisson() {
       String catelogJson = "";
       RLock lock = redissonClient.getLock("my-lock");
       lock.lock();
       try {
           return getStringListMap();
       } finally {
           lock.unlock();
       }
   }
   ```

## Redisson看门狗

1. 如果lock 不指定锁的超时时间，默认是看门狗时间 30s
2. 未指定超时时间，占锁成功，1/3 看门狗时间后 ，会开启一个定时任务， 设置超时时间为看门狗时间，实现锁的自动续期--》每10s 续期一次

## Redisson分布锁使用

1. 默认锁，使用看门狗时间30s，锁自动续期，默认也是非公平锁 

   ```java
   RLock lock = redissonClient.getLock("my-lock");
   lock.lock();
   ```

2. 自动解锁，加锁以后10秒钟解锁
  无需调用unlock方法手动解锁，不会自动续期，如果要手动解锁一定要确保业务执行时间小于锁的失效时间

  ```java
  lock.lock(10, TimeUnit.SECONDS);
  ```

3. 尝试加锁，最多等待100秒，上锁以后10秒自动解锁

  ```java
  boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
  ```

4. 读写锁

   ```java
   RReadWriteLock lock = redissonClient.getReadWriteLock("ReadWrite-Lock");
   RLock rLock = lock.readLock();
   RLock wLock = lock.writeLock();
   ```

5.  信号量（Semaphore）
    信号量为存储在redis中的一个数字，当这个数字大于0时，即可以调用`acquire()`方法增加数量，也可以调用`release()`方法减少数量，但是当调用`release()`之后小于0的话方法就会阻塞，直到数字大于0

    ```java
    RSemaphore park = redissonClient.getSemaphore("park");
    park.acquire(2);
    
    RSemaphore park = redissonClient.getSemaphore("park");
    park.release(2);
    ```

6.  闭锁（CountDownLatch）

    ```java
    //B执行5次后  A 才会执行todo
    
    //A 线程	 
    RCountDownLatch latch = redissonClient.getCountDownLatch("CountDownLatch");
    latch.trySetCount(5);  // 5次
    latch.await();
    //todo
    //B线程
    RCountDownLatch latch = redissonClient.getCountDownLatch("CountDownLatch");
    latch.countDown();
    ```
