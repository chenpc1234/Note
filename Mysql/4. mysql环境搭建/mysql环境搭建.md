# Linux安装mysql8

## 安装流程

1. 查看系统是否已经安装mariadb、 mysql
  rpm -qa | grep mariadb
  rpm -e --nodeps xxx
  rpm -qa|grep mysql
2. 安装MySQL依赖包libao
  yum install libaio
3. 创建MySQL安装目录和数据存放目录
  mkdir /usr/local/mysql
  mkdir /usr/local/mysql/mysqldb
4. 创建MySQL组：创建MySQL用户，并设置密码。
  useradd mysql
  passwd mysql
5. 将mysql目录的权限授给mysql用户和mysql组。
  chown -R mysql:mysql /usr/local/mysql
6. 解压到/usr/local/mysql  
7. 给mysql目录授权
   chmod -R 777 /usr/local/mysql
   chmod -R 777 /usr/local/mysql/mysqldb/
8. 在系统根目录的/etc创建MySQL的安装初始化配置文件my.cnf
   vim /etc/my.cnf

```tex
[mysqld]

# 设置3306端口

port=3306

# 设置mysql的安装目录

basedir=/usr/local/mysql

# 设置mysql数据库的数据的存放目录

datadir=/usr/local/mysql/mysqldb

# 允许最大连接数

max_connections=10000

# 允许连接失败的次数。这是为了防止有人从该主机试图攻击数据库系统

max_connect_errors=10

# 服务端使用的字符集默认为UTF8

character-set-server=utf8

# 创建新表时将使用的默认存储引擎

default-storage-engine=INNODB

# 默认使用“mysql_native_password”插件认证

default_authentication_plugin=mysql_native_password
[mysql]

# 设置mysql客户端默认字符集

default-character-set=utf8
[client]

# 设置mysql客户端连接服务端时默认使用的端口
port=3306
default-character-set=utf8
```

9. 进入MySQL安装目录的bin目录下
	cd /usr/local/mysql/bin
10 .执行命令，并记住随机密码
	./mysqld --initialize --console
	
11. 启动MySQL服务
	
	```bash
#进入support-files
cd /usr/local/mysql/support-files    
 #启动mysql服务  
	./mysql.server start
	#报without updating PID file错误
	chmod -R 777 /usr/local/mysql
	chmod -R 777 /usr/local//mysql/mysqldb
	```
	
11. 将MySQL加入系统进程中
    cp mysql.server /etc/init.d/mysqld

12. 重启服务
    service mysqld restart

13. 创建一个软连接到 /usr/bin。
    ln -s /usr/local/mysql/bin/mysql /usr/bin

14. 登陆修改登录密码 

    ```sh
    mysql -uroot -p
    ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
    
    
    error while loading shared libraries: libtinfo.so.5: cannot open shared object file: No such file or directory
    查看MYSQL的依赖
    ldd mysql 
    libtinfo.so.5 不存在则
    sudo ln -s /usr/lib64/libtinfo.so.6.1 /usr/lib64/libtinfo.so.5
    ```

15. 设置允许远程登录

    ```sql
    use mysql;
    update user set user.Host='%' where user.User='root';
    flush privileges;
    quit;
    ```

16. 重启MySQL服务
    service mysqld restart

17. 检查防火墙是否关闭或者检查端口3306

    1. 检查3306端口是否开放
       netstat -nupl|grep 3306
    2. 检查3306端口是否开放
       firewall -cmd --permanent --add-prot=3306/tcp
    3. 重启防火墙
       firewall -cmd --reload

## 设置远程访问

```sql
ALTER USER ‘root’@‘localhost’ IDENTIFIED WITH mysql_native_password BY ‘root’;
--修改密码
update user set password_expired='N' where user='root';    
update user set authentication_string=password('root') where user='root';
flush privileges;
-- 设置所有Ip 的远程访问
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%'IDENTIFIED BY 'root' WITH GRANT OPTION;
flush privileges;
--删除所有用户的远程访问
drop user root@'%';
--设置单个IP的远程访问 -- ip可以设置113.129.*.* 指定网段
 GRANT ALL PRIVILEGES ON *.* TO 'root'@'113.129.8.159' IDENTIFIED BY 'password' WITH GRANT OPTION;

例子：
只允许192.168.1.105这个地址登录访问，只能访问testwa这个库里面的所有表，
且只能用root账户及123uupp这个密码进行访问
grant all privileges on testwa.* to 'root'@'192.168.1.105' identified by '123uupp' with grant option;
```



# 主从复制集群

## 搭建前提

1. 每个节点都安装了相同版本的mysql
2. mysql节点之间能够ping通
3. 主库，从库都建立了相同database

## 搭建流程

1. 主库修改配置文件 my.cnf  添加

  ```tex
  #服务器唯一ID，不能重复
  server-id=131
  #启用二进制日志 日志文件名称
  log-bin=mysql-bin
  binlog-format=ROW
  #同步的数据库database名称，可以配置多个
  binlog-do-db=chenpc
  #binlog-ignore-db与binlog-do-db互斥表示不同步的数据库名称
  #binlog-ignore-db=mysql 
  ```

2. 主库添加用户 ，用于从库连接主库进行复制,或者授予其他用户slave权限

   ```sql
   CREATE USER 'master'@'%' IDENTIFIED BY '123456';
   FLUSH PRIVILEGES;
   GRANT REPLICATION SLAVE ON *.* TO master WITH GRANT OPTION;
   ```

3. 重启主库 记录主库状态

  ```tex
  service mysqld restart
  mysql -uroot -proot
  mysql> show master status;
  +------------------+----------+--------------+------------------+-------------------+
  | File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
  +------------------+----------+--------------+------------------+-------------------+
  | mysql-bin.000016 |      156 |              | mysql            |                   |
  +------------------+----------+--------------+------------------+-------------------+
  1 row in set (0.00 sec)
  ```

4. 从库配置my.cnf 文件

  ```tex
  #服务器ID 唯一
  server-id = 132
  #主服务器日志文件名
  log-bin=mysql-bin
  binlog-format=ROW
  log-slave-updates=true
  ## relay_log配置中继日志，日志名字可以随便取
  relay_log=edu-mysql-relay-bin
  ```

5. 重启从库，执行

  ```bash
  mysql -uroot -proot
  Mysql>stop slave;
  Mysql>change master to master_host='192.168.121.131', master_port=3306, master_user='master', master_password='123456', master_log_file='mysql-bin.000016', master_log_pos=156;
  start slave;
  
  #注意主库的相关信息与 2.3 步一致
  ```