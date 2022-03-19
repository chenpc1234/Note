```sql
--创建名称为 zhang3 的用户， 密码设为 123123；
create user 'username' identified by 'userpasssword';
--修改用户的密码
update mysql.user set password=password('newpassword') where user='username';
--修改用户名
update mysql.user set user='newname' where user='oldname';
--删除用户
drop user 'username';
--查询用户权限
show grants;
--授权
grant 权限 1,权限 2,…权限 n on 数据库名称.表名称 to 用户名@用户地址 identified by '连接口令';
--回收权限
revoke [权限 1,权限 2,…权限 n] on 库名.表名 from 用户名@用户地址;
--查看用和权限的相关信息的 SQL 指令
select host, user, password, select_priv, insert_priv,drop_priv from mysql.user;
```

```sql
mysql> select host, user, password, select_priv, insert_priv,drop_priv from mysql.user;
+-----------+------+-------------------------------------------+-------------+-------------+-----------+
| host      | user | password                                  | select_priv | insert_priv | drop_priv |
+-----------+------+-------------------------------------------+-------------+-------------+-----------+
| localhost | root | *81F5E21E35407D884A6CD4A731AEBFB6AF209E1B | Y           | Y           | Y         |
| heygo     | root | *81F5E21E35407D884A6CD4A731AEBFB6AF209E1B | Y           | Y           | Y         |
| 127.0.0.1 | root | *81F5E21E35407D884A6CD4A731AEBFB6AF209E1B | Y           | Y           | Y         |
| ::1       | root | *81F5E21E35407D884A6CD4A731AEBFB6AF209E1B | Y           | Y           | Y         |
+-----------+------+-------------------------------------------+-------------+-------------+-----------+
4 rows in set (0.04 sec)
```

host :表示连接类型

1. % 表示所有远程通过 TCP 方式的连接
2. IP 地址 如 (192.168.1.2,127.0.0.1) 通过制定 ip 地址进行的 TCP 方式的连接
3. 机器名 通过制定 i 网络中的机器名进行的 TCP 方式的连接
4. ::1 IPv6 的本地 ip 地址 等同于 IPv4 的 127.0.0.1
5. localhost 本地方式通过命令行方式的连接 ， 比如 mysql -u xxx -p 123xxx 方式的连接。

user:表示用户名
		同一用户通过不同方式链接的权限是不一样的。
password:密码
			所有密码串通过 password(明文字符串) 生成的密文字符串。 加密算法为 MYSQLSHA1 ， 不可逆 。
			mysql 5.7 的密码保存到 authentication_string 字段中不再使用 password 字段。
select_priv , insert_priv 等
			为该用户所拥有的权限。