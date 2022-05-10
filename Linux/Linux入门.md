# Linux入门

## Vmware安装及IP配置

1. 修改VMware 网络

   ```tex
   点击VMware快捷方式，右键打开文件所在位置→双击vmnetcfg.exe →  VMnet1 host-only → 
   修改subnet ip 设置网段：192.168.8.0 子网掩码：255.255.255.0 		
   在虚拟软件上 --My Computer → 选中虚拟机 → 右键  → settings  → network adapter  → host only →  ok
   ```

2. 修改windows网络

   ```tex
   windows --> 打开网络和共享中心 -> 更改适配器设置 -> 右键VMnet1 -> 属性 -> 双击IPv4 -> 
             设置windows的IP：192.168.8.100 子网掩码：255.255.255.0 -> 点击确定
   
   ```

3. 修改linux节点的网络

   ```bash
   vim /etc/sysconfig/network-scripts/ifcfg-eth0
   ```

   ```tex
   DEVICE="eth0"
   BOOTPROTO="static"           ###静态IP
   HWADDR="00:0C:29:3C:BF:E7"
   IPV6INIT="yes"
   NM_CONTROLLED="yes"
   ONBOOT="yes"
   TYPE="Ethernet"
   UUID="ce22eeca-ecde-4536-8cc2-ef0dc36d4a8c"
   IPADDR="192.168.8.101"       ###  IP
   NETMASK="255.255.255.0"      ### 
   GATEWAY="192.168.8.1"        ###网关
   ```

4. 修改主机名

   ```bash
   vim /etc/sysconfig/network		
   
   NETWORKING=yes 
   HOSTNAME=node1    ###
   
   ##centos8 以下方式修改
   hostnamectl set-hostname node
   ```

5. 修改主机名和IP的映射关系          

   ```BASH
   vim /etc/hosts
   192.168.8.101	node1
   ```

6. 关闭防火墙，内外的防火墙都要关闭

   ```bash
   #查看防火墙状态
   service iptables status
   #关闭防火墙
   service iptables stop
   #查看防火墙开机启动状态
   chkconfig iptables --list
   #关闭防火墙开机启动
   chkconfig iptables off
   ```

## SSH免登陆设置

```tex
#进入到ROOT的home目录
	cd ~/.ssh
执行
ssh-keygen -t rsa 

执行完这个命令后,会生成两个文件id_rsa（私钥）、id_rsa.pub（公钥）
	ssh-copy-id localhost

A机SSH免登陆B机 
   A机生成秘钥 
   A机把公钥发给B机     ssh-copy-id  B机IP
```

## JDK安装

```tex
1. 上传JDK
	
2. 解压jdk 到指定文件夹
	   #创建文件夹
         		mkdir /usr/java
	   #解压
		tar -zxvf jdk-7u55-linux-i586.tar.gz -C /usr/java/		
3. 将java添加到环境变量中
	vim /etc/profile
	#在文件最后添加
	export JAVA_HOME=/usr/java/jdk1.7.0_55
	export PATH=$PATH:$JAVA_HOME/bin
4. #刷新配置
	source /etc/profile
```

## Mysql安装

```tex
1. 下载包   RPM Bundle
		https://dev.mysql.com/downloads/mysql/
2.查询服务器是否已安装mysql或者mysql的依赖
        rpm -qa|grep mysql
        卸载
        rpm -e --nodeps mysql-libs-5.1.71-1.el6.x86_64 

3. 上传至服务器并解压文件
	tar -xf mysql-5.7.12-1.el6.x86_64.rpm-bundle.tar

4. 依次安装
    1. rpm -ivh mysql-community-common-5.7.12-1.el6.x86_64.rpm
    2. rpm -ivh mysql-community-libs-5.7.12-1.el6.x86_64.rpm
    3. rpm -ivh mysql-community-devel-5.7.12-1.el6.x86_64.rpm
    4. rpm -ivh mysql-community-client-5.7.12-1.el6.x86_64.rpm
    5. rpm -ivh mysql-community-server-5.7.12-1.el6.x86_64.rpm


5. 启动MySQL：service mysqld start
	vim /etc/my.cnf，在文件末尾加上：skip-grant-tables，保存后重启MySQL服务：service mysqld restart，然后重新登录。
6. 登录：mysql -u root -p，初次登录密码为空，直接回车
	use mysql
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

## Crontab定时器

```tex
*　　*　　*　　*　　*　　command 
分　 时　 日　 月　 周　 命令 
第1列表示分钟1～59 每分钟用*或者 */1表示 
第2列表示小时1～23（0表示0点） 
第3列表示日期1～31 
第4列表示月份1～12 
第5列标识号星期0～6（0表示星期天） 
第6列要运行的命令 
```

crontab文件的一些例子： 

1. 30 21 * * * /usr/local/etc/rc.d/lighttpd restart 
   上面的例子表示每晚的21:30重启apache。 
2. 45 4 1,10,22 * * /usr/local/etc/rc.d/lighttpd restart 
   上面的例子表示每月1、10、22日的4 : 45重启apache。 
3. 10 1 * * 6,0 /usr/local/etc/rc.d/lighttpd restart 
   上面的例子表示每周六、周日的1 : 10重启apache。 
4. 0,30 18-23 * * * /usr/local/etc/rc.d/lighttpd restart 
   上面的例子表示在每天18 : 00至23 : 00之间每隔30分钟重启apache。 
5. 0 23 * * 6 /usr/local/etc/rc.d/lighttpd restart 
   上面的例子表示每星期六的11 : 00 pm重启apache。 
6. 0 11 4 * mon-wed /usr/local/etc/rc.d/lighttpd restart 
   每月的4号与每周一到周三的11点重启apache 
7. 0 4 1 jan * /usr/local/etc/rc.d/lighttpd restart 
   一月一号的4点重启apache 