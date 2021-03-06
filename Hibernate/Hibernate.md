# Hibernate

​			Hibernate是一个全自动的orm框架，hibernate可以自动生成SQL语句，自动执行，使得Java程序员可以随心所欲的使用对象编程思维来操纵数据库。

## hibernate-mapping

​	数据库表对应实体类的映射文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
<!-- 
    Mapping file autogenerated by MyEclipse Persistence Tools
-->
<hibernate-mapping>
    <class name="com.sxt.domain.Test" table="TEST" schema="WS">
        <id name="id" type="java.lang.String">
            <column name="ID" length="20" />
            <generator class="assigned" />
        </id>
        <property name="name" type="java.lang.String">
            <column name="NAME" length="20" />
        </property>
        <property name="pwd" type="java.lang.String">
            <column name="PWD" length="20" />
        </property>
    </class>
</hibernate-mapping>

```

## 接口

- Configuration类：配置Hibernate、根启动Hibernate、创建SessionFactory对象
- SessionFactory接口：初始化Hibernate、数据源的代理、创建Session对象
- Session接口：负责保存、更新、删除、加载和查询对象
- Transaction接口：管理事务
- Query和Criteria接口：执行数据库查询
- 主键的生成策略：IdentifierGenerator
- 本地方言：Dialect（抽象类）
- 缓冲机制：Cache和CacheProvider
- JDBC连接管理：ConnectionProvider
- 事务管理：TransactionFactory、Transaction、TansactionManagerLookup
- ORM策略：ClassPersister及其子接口
- 访问策略：PopertyAccessor
- 创建代理：ProxyFactory
- 客户化映射类型：UserType和CompositeUserType

## 缓存机制

### Hibernate缓存的作用：

​			Hibernate是一个持久层框架，经常访问物理数据库，为了降低应用程序对物理数据源访问的频次，从而提高应用程序的运行性能。缓存内的数据是对物理数据源中的数据的复制，应用程序在运行时从缓存读写数据，在特定的时刻或事件会同步缓存和物理数据源的数据

### Hibernate缓存分类：

  		Hibernate缓存包括两大类：Hibernate一级缓存和Hibernate二级缓存。

- Hibernate一级缓存又称为“Session的缓存”，它是内置的，不能被卸载（不能被卸载的意思就是这种缓存不具有可选性，必须有的功能，不可以取消session缓存）。由于Session对象的生命周期通常对应一个数据库事务或者一个应用事务，因此它的缓存是事务范围的缓存。第一级缓存是必需的，不允许而且事实上也无法卸除。在第一级缓存中，持久化类的每个实例都具有唯一的OID。 
- Hibernate二级缓存又称为“SessionFactory的缓存”，由于SessionFactory对象的生命周期和应用程序的整个过程对应，因此Hibernate二级缓存是进程范围或者集群范围的缓存，有可能出现并发问题，因此需要采用适当的并发访问策略，该策略为被缓存的数据提供了事务隔离级别。第二级缓存是可选的，是一个可配置的插件，在默认情况下，SessionFactory不会启用这个插件。

### Hibernate二级缓存数据特点

**适合放入二级缓存的数据应满足如下条件**

1. 很少被修改的数据 　　
2. 不是很重要的数据，允许出现偶尔并发的数据 　　
3. 不会被并发访问的数据 　　
4. 常量数据 

**不适合放入二级缓存**

1. 经常被修改的数据 　　
2. 绝对不允许出现并发访问的数据，如财务数据，绝对不允许出现并发 　　
3. 与其他应用共享的数据。 

### Hibernate查找对象如何应用缓存？

1. 当Hibernate根据ID访问数据对象的时候，首先从Session一级缓存中查；
2. 一级缓存查不到，如果配置了二级缓存，那么从二级缓存中查；
3. 如果都查不到，再查询数据库，把结果按照ID放入到缓存。删除、更新、增加数据的时候，同时更新缓存

### Hibernate管理缓存实例

​		无论何时，我们在管理Hibernate缓存（Managing the caches）时，当你给save()、update()或saveOrUpdate()方法传递一个对象时，或使用load()、 get()、list()、iterate() 或scroll()方法获得一个对象时, 该对象都将被加入到Session的内部缓存中。 
当随后flush()方法被调用时，对象的状态会和数据库取得同步。 如果你不希望此同步操作发生，或者你正处理大量对象、需要对有效管理内存时，你可以调用evict() 方法，从一级缓存中去掉这些对象及其集合。 