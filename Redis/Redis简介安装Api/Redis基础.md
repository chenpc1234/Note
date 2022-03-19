# 简介

Redis:REmote DIctionary Server(远程字典服务器)

- 完全开源免费的，用C语言编写的，遵守BSD协议
- 高性能的(key/value)分布式内存数据库，基于内存运行，并支持持久化的NoSQL数据库，被称为数据结构服务器。

## 特点

1. 速度快，基于内存运行
2. 支持数据的持久化：可以将内存中的数据保持在磁盘中，重启的时候可以再次加载进行使用
3. 存储类型多样：Redis不仅仅支持简单的key-value类型的数据，同时还提供list，set，zset，hash等数据结构的存储
4. 支持数据的备份：即master-slave模式的数据备份
5. 支持事务
   - MULTI：开启事务，总是返回OK
   - EXEC：提交事务
   - DISCARD：放弃事务（放弃提交执行）
   - WATCH：监控
   - QUEUED：将命令加入执行的队列

# Redis线程模型

文件事件处理器的结构

- 多个 socket
- IO 多路复用程序
- 文件事件分派器
- 事件处理器（连接应答处理器、命令请求处理器、命令回复处理器）

## 线程模型

​		多个 socket 可能会并发产生不同的操作，每个操作对应不同的文件事件，但是 IO多路复用程序会监听多个 socket，会将产生事件的 socket 放入队列中排队，事件分派器每次从队列中取出一个 socket，根据 socket 的事件类型交给对应的事件处理器进行处理。
![img](Redis基础.assets/2136379-20200830233359116-1537526582.png)

## 一次客户端与redis的完整通信过程

### 建立连接

1. 首先，redis 服务端进程初始化的时候，会将 server socket 的 AE_READABLE 事件与连接应答处理器关联。
2. 客户端 socket01 向 redis 进程的 server socket 请求建立连接，此时 server socket 会产生一个 AE_READABLE 事件，IO 多路复用程序监听到 server socket 产生的事件后，将该 socket 压入队列中。
3. 文件事件分派器从队列中获取 socket，交给连接应答处理器。
4. 连接应答处理器会创建一个能与客户端通信的 socket01，并将该 socket01 的 AE_READABLE 事件与命令请求处理器关联。

![](Redis基础.assets/2136379-20200830233422768-1205881078.png)

### 执行一个set请求

1. 客户端发送了一个 set key value 请求，此时 redis 中的 socket01 会产生 AE_READABLE 事件，IO 多路复用程序将 socket01 压入队列。
2. 此时事件分派器从队列中获取到 socket01 产生的 AE_READABLE 事件，由于前面 socket01 的 AE_READABLE 事件已经与命令请求处理器关联，因此事件分派器将事件交给命令请求处理器来处理。命令请求处理器读取 socket01 的 key value 并在自己内存中完成 key value 的设置。
3. 操作完成后，它会将 socket01 的 AE_WRITABLE 事件与命令回复处理器关联。
4. 如果此时客户端准备好接收返回结果了，那么 redis 中的 socket01 会产生一个 AE_WRITABLE 事件，同样压入队列中，事件分派器找到相关联的命令回复处理器，由命令回复处理器对 socket01 输入本次操作的一个结果，比如 ok，之后解除 socket01 的 AE_WRITABLE 事件与命令回复处理器的关联。



##  **Redis6.0 多线程体现**

​		Redis 6 中的多线程 主要在处理 网络 I/O 方面，对网络事件进行监听，分发给 work thread 进行处理，处理完以后将主动权交还给 主线程，进行 执行操作，当然后续还会有，执行后依然交由 work thread 进行响应数据的 socket write 操作。



![img](Redis基础.assets/1031302-20201109104625665-1682010410.png)

# 为什么Redis的性能高？

1. 完全基于内存，绝大部分请求是纯粹的内存操作，非常快速。
2. 数据结构简单，对数据操作也简单，Redis 中的数据结构是专门进行设计的；
3. 采用单线程，避免了不必要的上下文切换和竞争条件，也不存在多进程或者多线程导致的切换而消耗 CPU，不用去考虑各种锁的问题，不存在加锁释放锁操作，没有因为可能出现死锁而导致的性能消耗；(但是redis6.0已经开始使用多线程了，不过是在网络层面)
4. 使用多路 I/O 复用模型，非阻塞 IO； 

# 安装及部署                                                                                                                                    

## linux下安装

1. 官网下载最新包

   [redis下载地址](http://www.redis.cn/download.html)

2. 解压到目录  /opt/redis 

3. 在/opt/redis 执行 make命令 若命令不存在  则需要安装gcc，执行一下就可以了：

   ```sh
   yum -y list gcc*
   yum -y install gcc automake autoconf libtool make
   yum install gcc gcc-c++
   make
   ```

4.  make执行完成，进入 src 目录下执行 

	```sh
	/opt/redis/src/redis-server&
	```
	
5. 修改 redis-conf  配置文件

   ```tex
   将daemonize改为yes
   也将bind注释，
   protected-mode设置为no。
   ```

7. 进入 src 目录下  执行

   ```sh
    ./redis-cli 
   ```

## docker安装

```sh
docker run -p 6379:6379 -v /chen/myredis/data:/data -v /chen/myredis/conf/redis.conf:/usr/local/etc/redis/redis.conf  -d redis:3.2 redis-server /usr/local/etc/redis/redis.conf --appendonly yes
```

```tex
命令示意
docker run 
-p 6379:6379 					#端口映射 宿主机12345端口:容器3306端口
-v /chen/myredis/data:/dat 		#数据目录，容器数据卷	宿主机目录:容器目录
-v /chen/myredis/conf/redis.conf:/usr/local/etc/redis/redis.conf  
								#配置目录，容器数据卷   宿主机目录:容器目录
-d 								#守护进程启动
redis:3.2						#镜像名 redis:tag版本
redis-server /usr/local/etc/redis/redis.conf --appendonly yes     # Shell命令 启动redis 配置文件位置  开启aof
```

# 基础操作API

## key 操作

```tex
SELECT 1   				#选择第2个数据库  redis安装时默认安装16个库 编号0-15
KEYS *    				#查看当前库的key信息
keys a*   				#查看当前库a开头的key信息
keys a?  				#查看当前库a？的key信息
exists [key] 			#判断某个key是否存在
ttl [key]  				#查看还有多少秒过期，-1表示永不过期，-2表示已过期
expire [key] [second]  	#为给定的key设置过期时间
type [key] 				#查看你的key是什么类型
move [key] [db] 		#当前库就没有了，被移除了
DBSIZE   				#查看当前库下的key的数量
------------------------------------------------------------------------------------------
flushdb  				#清空当前库
flushall 				#清空所有库！！！
----value-------------------------------
object encoding [key] 		查看value的编码
```

## String Api

```tex
GET key						#获取指定 key 的值。	   
SET key value				#设置指定 key 的值	   
SETNX key value				#只有在 key 不存在时设置 key 的值。	   
SETEX key seconds value		#将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以秒为单位)。
STRLEN key					#返回 key 所储存的字符串值的长度。	   
INCR key					#将 key 中储存的数字值增一。	   
DECR key					#将 key 中储存的数字值减一。
INCRBY key increment		#将 key 所储存的值加上给定的增量值（increment） 。	   
DECRBY key decrement		#key 所储存的值减去给定的减量值（decrement） 。	   
APPEND key value			#如果 key 已经存在并且是一个字符串， APPEND 命令将指定的 value 追加到该 key 原来值（value）的末尾
-------------------------------------------------------------------------------------------------------
PSETEX key milliseconds value	#这个命令和 SETEX 命令相似，但它以毫秒为单位设置 key 的生存时间，而不是像 SETEX 命令那									样，以秒为单位。
MSET key value [key value …]	#同时设置一个或多个 key-value 对。	   
MGET key1 [key2…]				#获取所有(一个或多个)给定 key 的值。	   
MSETNX key value [key value …]	#同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在。
-------------------------------------------------------------------------------------------------------
GETRANGE key start end		#返回 key 中字符串值的子字符	   
SETRANGE key offset value	#用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始。	   
GETSET key value			#将给定 key 的值设为 value ，并返回 key 的旧值(old value)。	   
GETBIT key offset			#对 key 所储存的字符串值，获取指定偏移量上的位(bit)。	   
SETBIT key offset value		#对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)。	  	
---
INCRBYFLOAT key increment	#将 key 所储存的值加上给定的浮点增量值（increment） 。	    	   
```

## Hash Api

```tex
HSET key field value						#将哈希表 key 中的字段 field 的值设为 value 。
HGET key field								#获取存储在哈希表中指定字段的值。	   
-- 批量------------------------------------------------------------------------------------------
HMSET key field1 value1 [field2 value2 ]	#同时将多个 field-value (域-值)对设置到哈希表 key 中。	
HMGET key field1 [field2]					#获取所有给定字段的值	   
-- 取值------------------------------------------------------------------------------------------
HGETALL key									#获取在哈希表中指定 key 的所有字段和值	   
HKEYS key									#获取所有哈希表中的字段	   
HVALS key									#获取哈希表中所有值。	   
HLEN key									#获取哈希表中字段的数量	   
-- 删除------------------------------------------------------------------------------------------
HDEL key field1 [field2]					#删除一个或多个哈希表字段
-- 存在性------------------------------------------------------------------------------------------
HEXISTS key field							#查看哈希表 key 中，指定的字段是否存在.
HSETNX key field value						#只有在字段 field 不存在时，设置哈希表字段的值。		   
-- 增加------------------------------------------------------------------------------------------
HINCRBY key field increment					#为哈希表 key 中的指定字段的整数值加上增量 increment 。	   
HINCRBYFLOAT key field increment			#为哈希表 key 中的指定字段的浮点数值加上增量 increment 。	   
--迭代 分页------------------------------------------------------------------------------------------
HSCAN key cursor [MATCH pattern] [COUNT count]	迭代哈希表中的键值对。
```

## List Api

```txt
LSET key index value			#通过索引设置列表元素的值
------------------------------------------------------------------------------------------
LPUSH key value1 [value2]		#将一个或多个值插入到列表头部
RPUSH key value1 [value2]		#在列表尾部添加一个或多个值
------------------------------------------------------------------------------------------
LPOP key	移出并获取列表的第一个元素	 
RPOP key	移除列表的最后一个元素，返回值为移除的元素。	   
BLPOP key1 [key2 ] timeout		#移出并获取列表的第一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
BRPOP key1 [key2 ] timeout		#移出并获取列表的最后一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。 
------------------------------------------------------------------------------------------
BRPOPLPUSH source destination timeout	从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。	   
LINDEX key index	通过索引获取列表中的元素	   
LINSERT key BEFORE/AFTER pivot value	在列表的元素前或者后插入元素	   
LLEN key	获取列表长度	   
------------------------------------------------------------------------------------------      
LRANGE key start stop	获取列表指定范围内的元素	   
LREM key count value	移除列表元素	   
------------------------------------------------------------------------------------------  
LTRIM key start stop	对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
----------------------------------------------------------------------------------------	   
RPOPLPUSH source destination	移除列表的最后一个元素，并将该元素添加到另一个列表并返回	   
LPUSHX key value	将一个值插入到已存在的列表头部   
RPUSHX key value	为已存在的列表添加值	 
```

## Set Api

```tex
SADD key member1 [member2]			#向集合添加一个或多个成员
SISMEMBER key member				#判断 member 元素是否是集合 key 的成员	   
SMEMBERS key						#返回集合中的所有成员  
SCARD key							#获取集合的成员数
------集合处理 交集、差集、合集-------------------------------------------------------   
SDIFF key1 [key2]					#返回给定所有集合的差集	   
SDIFFSTORE destination key1 [key2]	#返回给定所有集合的差集并存储在 destination 中	   
SINTER key1 [key2]					#返回给定所有集合的交集	   
SINTERSTORE destination key1 [key2]	#返回给定所有集合的交集并存储在 destination 中	   
SUNION key1 [key2]					#返回所有给定集合的并集
SUNIONSTORE destination key1 [key2]	#所有给定集合的并集存储在 destination 集合中	   
-----移动 移除 随机返回---------------------------------------------------------------
SMOVE source destination member		#将 member 元素从 source 集合移动到 destination 集合	   
SPOP key							#移除并返回集合中的一个随机元素	   
SRANDMEMBER key [count]				#返回集合中一个或多个随机数	   
SREM key member1 [member2]			#移除集合中一个或多个成员  
SSCAN key cursor [MATCH pattern] [COUNT count]	-----迭代集合中的元素
```

## ZSet Api

```tex
Zset 会根据分数[score]对每个成员[member]进行排序,每个成员都有对应的索引[index]
------新增----------------------------------------------------------------------------
ZADD key score1 member1 [score2 member2] 		#向有序集合添加一个或多个成员，或者更新已存在成员的分数	   
ZCARD key										#获取有序集合的成员数
-----加分----------------------------------------------------------------------------
ZINCRBY key increment member					#指定的key集合中成员member分数+increment
----查成员信息-----------------------------------------------------------------------------------
ZRANK key member								#查index,返回有序集合中指定成员的索引 ，分数从低到高
ZREVRANK key member								#返回有序集合中指定成员的排名	，分数从高到低
ZSCORE key member								#查score,返回有序集中，成员的分数值	   
---根据[index member score]区间查成员列表------------------------------------------------------
ZRANGE key start stop [WITHSCORES]				#通过索引区间返回有序集合指定区间内的成员，分数从低到高
ZREVRANGE key start stop [WITHSCORES]			#同上，分数从高到底

ZRANGEBYLEX key min max [LIMIT offset count]	#通过字典区间返回有序集合的成员	   

ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT]	#通过分数返回有序集合指定区间内的成员，分数从低到高排
ZREVRANGEBYSCORE key max min [WITHSCORES]		#同上，分数从高到低排序
---查成员数量---------------------------------------------------------------------------------
ZCOUNT key min max								#指定区间分数[score]的成员数,前闭后闭
ZLEXCOUNT key min max							#计算指定字典区间[member]内成员数量，[min 包含 (min不包含
-----交集/并集------------------------------------
ZINTERSTORE destination numkeys key [key …]		#计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key中
ZUNIONSTORE destination numkeys key [key …]		#计算给定的一个或多个有序集的并集，并存储在新的 key 中	   
--移除------------------------------------
ZREM key member [member …]						#移除有序集合中的一个或多个成员	   
ZREMRANGEBYLEX key min max						#移除有序集合中给定的字典区间的所有成员	   
ZREMRANGEBYRANK key start stop					#移除有序集合中给定的排名区间的所有成员	   
ZREMRANGEBYSCORE key min max					#移除有序集合中给定的分数区间的所有成员

ZSCAN key cursor [MATCH pattern] [COUNT count]	#迭代有序集合中的元素（包括元素成员和元素分值）	 
```
