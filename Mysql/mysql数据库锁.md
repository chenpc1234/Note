# Mysql锁

```sql
--手工测试
--给表加锁测试  读、写
lock table tablename read,tablename write;
--给表解锁
unlock tables;
--查询表上的锁
show open tables;
```

## **按照操作类型区分**

- ### 读锁    

  共享锁  读锁阻塞写不阻塞读

- ### 写锁   

    排它锁  写锁阻塞读写

## **按照锁的作用范围分类**

- ### 表锁
  
  - 偏向**MYISAM**存储引擎，开销小，加锁快；
  - 不会产生死锁；
  - 锁定粒度大；
  - 发生锁冲突的概率高，并发度很低
    ```text
    session1 给表添加写锁
    lock table tablename write;
    ---------------------------------
    session1 能CURD tablename
    session1 不能CURD othertable  会报错必须先解锁
    -------------------------------
    session2 能Retrieve     tablename
    session2 不能CUD tablename  处于阻塞状态 等待session1 解锁
    session2 能CURD othertable;
    ```
    ```text
    session1 给表添加读锁
    lock table tablename read;
    ---------------------------------
    session1 能Retrieve tablename
    session1 不能CUD tablename  会报错
    session1 不能CURD othertable  会报错必须先解锁tablename
    -------------------------------
    session2 不能CURD tablename  处于阻塞状态 等待session1 解锁
    session2 能CURD othertable;
    ```
  ```
  
  ```
  
- ### 行锁（支持事务）
  
  - 偏向**InnoDB**存储引擎，开销大，加锁慢
    - InnoDB和MYISAM区别  1.支持行锁  2.支持事务
      - ACID  atomiciy 原子性、Consistent 一致性 、Isolation 隔离性 、Durable持久性
      - 并发事务带来的问题： 更新丢失、脏读、不可重复读、幻读
  - 会产生死锁；
  - 锁定粒度小；
  - 发生锁冲突的概率低，并发度很高
    ```txt
    session1 开启事务，修改 test_innodb_lock 中的数据，varchar 不用 ’ ’ ，
    导致系统自动转换类型，导致索引失效
    ```
  - #### 间隙锁
    
  - 当我们用范围条件而不是相等条件检索数据，并请求共享或排他锁时，InnoDB会给符合条件的已有数据记录的索引项加锁；对于键值在条件范围内但并不存在的记录，叫做“间隙（GAP）”
  - InnoDB也会对这个“间隙”加锁，这种锁机制是所谓的间隙锁（Next-Key锁）
    间隙锁的危害
    - 因为Query执行过程中通过过范围查找的话，他会锁定整个范围内所有的索引键值，即使这个键值并不存在。
    - 间隙锁有一个比较致命的弱点，就是**当锁定一个范围键值之后，即使某些不存在的键值也会被无辜的锁定，而造成在锁定的时候无法插入锁定键值范围内的任何数据**。在某些场景下这可能会对性能造成很大的危害
- ### 页锁
