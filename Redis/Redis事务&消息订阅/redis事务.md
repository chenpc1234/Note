# redis 事务 

## 简介

​	事务可以一次执行多个命令， 并且带有以下两个重要的保证：

- 事务是一个单独的隔离操作：事务中的所有命令都会序列化、按顺序地执行。事务在执行的过程中，不会被其他客户端发送来的命令请求所打断。
- 事务是一个原子操作：事务中的命令要么全部被执行，要么全部都不执行。[EXEC](http://www.redis.cn/commands/exec.html) 命令负责触发并执行事务中的所有命令

## 相关命令

| 命令              | 描述                                                         |
| ----------------- | ------------------------------------------------------------ |
| MULTI             | 标记一个事务块的开始。                                       |
| EXEC              | 执行所有事务块内的命令。                                     |
| DISCARD           | 取消事务，放弃执行事务块内的所有命令。                       |
| WATCH key [key …] | 监视一个(或多个) key ，如果在事务执行之前这个(或这些) key 被其他命令所改动，那么事务将被打断。 |
| UNWATCH           | 取消 WATCH 命令对所有 key 的监视。                           |

​		**WATCH使得 EXEC命令需要有条件地执行： 事务只能在所有被监视键都没有被修改的前提下执行， 如果这个前提不能满足的话，事务就不会被执行**。

​		WATCH命令可以被调用多次。 对键的监视从 WATCH执行之后开始生效， 直到调用 EXEC为止。

## 特点

1. 单独的隔离操作：
   - 事务中的所有命令都会序列化、按顺序地执行。事务在执行的过程中，不会被其他客户端发送来的命令请求所打断。
2. 没有隔离级别的概念：
   - 队列中的命令没有提交之前都不会实际的被执行，因为事务提交前任何指令都不会被实际执行， 也就不在”事务内的查询要看到事务里的更新，在事务外查询不能看到”这个让人万分头痛的问题
3. 不保证原子性：
   - redis同一个事务中如果有一条命令执行失败，其后的命令仍然会被执行，没有回滚

## 为什么 Redis 不支持回滚（roll back）

- Redis 命令只会因为错误的语法而失败（并且这些问题不能在入队时发现），或是命令用在了错误类型的键上面：这也就是说，从实用性的角度来说，失败的命令是由编程错误造成的，而这些错误应该在开发的过程中被发现，而不应该出现在生产环境中。
- 因为不需要对回滚进行支持，所以 Redis 的内部可以保持简单且快速。

## Redis 脚本和事务

从定义上来说， Redis 中的脚本本身就是一种事务， 所以任何在事务里可以完成的事， 在脚本里面也能完成。 并且一般来说， 使用脚本要来得更简单，并且速度更快

# redis消息订阅

| 命令                                      | 描述                               |
| ----------------------------------------- | ---------------------------------- |
| PSUBSCRIBE pattern [pattern …]            | 订阅一个或多个符合给定模式的频道。 |
| PUBSUB subcommand [argument [argument …]] | 查看订阅与发布系统状态。           |
| PUBLISH channel message                   | 将信息发送到指定的频道。           |
| PUNSUBSCRIBE [pattern [pattern …]]        | 退订所有给定模式的频道。           |
| SUBSCRIBE channel [channel …]             | 订阅给定的一个或多个频道的信息。   |
| UNSUBSCRIBE [channel [channel …]]         | 指退订给定的频道。                 |

以下实例演示了发布订阅是如何工作的。在我们实例中我们创建了订阅频道名为 redisChat:

```bash
//client-1  订阅频道  redisChat
redis 127.0.0.1:6379> SUBSCRIBE redisChat
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "redisChat"
3) (integer) 1

//client-2 推送消息 到redisChat
redis 127.0.0.1:6379> PUBLISH redisChat "Redis is a great caching technique"
(integer) 1
redis 127.0.0.1:6379> PUBLISH redisChat "Learn redis by runoob.com"
(integer) 1

#client-1 订阅者的客户端会显示如下消息
1) "message"
2) "redisChat"
3) "Redis is a great caching technique"
1) "message"
2) "redisChat"
3) "Learn redis by runoob.com"
```

