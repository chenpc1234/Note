# Hadoop

## Hadoop伪分布式安装及配置

1. 需要配置好 IP及JDK，上传hadoop压缩包。解压到特定目录

2. 修改hadoop 配置文件，路径/hadoop-2.2.0/etc/hadoop

   1. 修改hadoop-env.sh 添加JDK环境变量

      ```sh
      export JAVA_HOME=/usr/java/jdk1.7.0_67
      ```

   2. 修改 core-site.xml

      ```xml
      <!-- 指定定HDFS的（NameNode）的地址 -->
      <property>
      	<name>fs.defaultFS</name>
      	<value>hdfs://node1:9000</value>
      </property>
      	<!-- 指定hadoop运行时产生文件的存储目录 -->
      <property>
      	<name>hadoop.tmp.dir</name>
      	<value>/node/hadoop-2.2.0/tmp</value>
      </property>
      ```

   3. 修改 hdfs-site.xml 

      ```xml
      <!-- 指定HDFS副本的数量  节点数集群一般为多个-->
      <property>
          <name>dfs.replication</name>
          <value>1</value>
      </property>
      ```

   4. 修改mapred-site.xml

      ```sh
      <!-- 指定mapreduce运行在yarn上 -->
      <property>
          <name>mapreduce.framework.name</name>
          <value>yarn</value>
      </property>
      ```

   5. 修改  yarn-site.xml

      ```xml
      <!-- 指定YARN的老大（ResourceManager）的地址 -->
      <property>
      	<name>yarn.resourcemanager.hostname</name>
      	<value>node1</value>
      	</property>
      <!-- mapreduce获取数据的方式 shuffle-->
      <property>
          <name>yarn.nodemanager.aux-services</name>
          <value>mapreduce_shuffle</value>
      </property>
      ```

3. 将hadoop配置到系统环境变量中 并刷新缓存

   ```bash
   vi /etc/profile 	
   export HADOOP_HOME=/itcast/hadoop-2.4.1
   export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
   source /etc/profile
   ```

4. 格式化namenode（是对namenode进行初始化）

   ```bash
   hdfs namenode -format (hadoop namenode -format)
   ```

5. 启动hadoop

   ```bash
   先启动HDFS
   start-dfs.sh
   再启动YARN
   start-yarn.sh
   ```

6. 验证

   ```tex
     jps
   //结果如下6条数据
   27408 NameNode
   28218 Jps
   27643 SecondaryNameNode
   28066 NodeManager
   27803 ResourceManager
   27512 DataNode
   ```

7. 登陆网页http://node1:50070 （HDFS管理界面）http://node1:8088 （MR管理界面）验证

8. 配置SSH免登陆

   ```tex
   #进入到ROOT的home目录
   	cd ~/.ssh
   执行   ssh-keygen -t rsa 
   执行完这个命令后，会生成两个文件id_rsa（私钥）、id_rsa.pub（公钥）
   
   	拷贝公钥到文件~/.ssh/authorized_keys
   	cp id_rsa.pub authorized_keys
                 或者
   	ssh-copy-id node1
   
   
   A机想SSH免登陆B机 
      A机生成秘钥 
      A机把公钥发给B机     ssh-copy-id  B机IP
   ```

9. 简单的MapReduce测试

   ```sh
   1.hadoop安装目录下/node/hadoop-2.2.0/share/hadoop/mapreduce有很多jar包
                      hadoop-mapreduce-examples-2.2.0.jar 是一个样例jar包
    
   2. 文件计数，统计每个单词在文档中存在的次数
   hadoop  jar /node/hadoop-2.2.0/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.2.0.jar   methodname 文件名  返回结果文件名
   一般文件和结果文件都保存在hdfs上 
   
   例如： 
   hadoop jar hadoop-mapreduce-examples-2.2.0.jar wordcount hdfs://node1:9000/words hdfs://node1:9000/wordscount
   ```

## 远程调试

### JPDA 简介

​			Sun Microsystem 的 Java Platform Debugger Architecture (JPDA) 技术是一个多层架构，使您能够在各种环境中轻松调试 Java 应用程序。JPDA 由两个接口（分别是 JVM Tool Interface 和 JDI）、一个协议（Java Debug Wire Protocol）和两个用于合并它们的软件组件（后端和前端）组成。它的设计目的是让调试人员在任何环境中都可以进行调试。

### JDWP 设置

​			JVM本身就支持远程调试，Eclipse也支持JDWP，只需要在各模块的JVM启动时加载以下参数：

- dt_socket            表示使用套接字传输。

- address=8000    JVM在8000端口上监听请求，这个设定为一个不冲突的端口即可。

- server=y             y表示启动的JVM是被调试者。如果为n，则表示启动的JVM是调试器。

- suspend=y        y表示启动的JVM会暂停等待，直到调试器连接上才继续执行。

  suspend=n      则JVM不会暂停等待。

- 需要在$HADOOP_HOME/etc/hadoop/hadoop-env.sh文件的最后添加你想debug的进程
  - #远程调试namenode
    export HADOOP_NAMENODE_OPTS="-agentlib:jdwp=transport=dt_socket,address=8888,server=y,suspend=y"
  - #远程调试datanode
    export HADOOP_DATANODE_OPTS="-agentlib:jdwp=transport=dt_socket,address=9888,server=y,suspend=y"
  - #远程调试RM
    export YARN_RESOURCEMANAGER_OPTS="-agentlib:jdwp=transport=dt_socket,address=10888,server=y,suspend=y"
  - #远程调试NM
    export YARN_NODEMANAGER_OPTS="-agentlib:jdwp=transport=dt_socket,address=10888,server=y,suspend=y"

## HDFS

### RPC通讯

​				RPC——远程过程调用协议，它是一种通过网络从远程计算机程序上请求服务，而不需要了解底层网络技术的协议。RPC协议假定某些传输协议的存在，如TCP或UDP，为通信程序之间携带信息数据。在OSI网络通信模型中，RPC跨越了传输层和应用层。RPC使得开发包括网络分布式多程序在内的应用程序更加容易。

​				RPC采用客户机/服务器模式。请求程序就是一个客户机，而服务提供程序就是一个服务器。首先，客户机调用进程发送一个有进程参数的调用信息到服务进程，然后等待应答信息。在服务器端，进程保持睡眠状态直到调用信息的到达为止。当一个调用信息到达，服务器获得进程参数，计算结果，发送答复信息，然后等待下一个调用信息，最后，客户端调用进程接收答复信息，获得进程结果，然后调用执行继续进行。

###  NameNode

- metedata：元数据 （存放在namenode的内存中）

- fsimage:元数据镜像文件。存储某一时段NameNode内存元数据信息。

- edits:操作日志文件。

- fstime:保存最近一次checkpoint的时间

  

1. Namenode始终在内存中保存metedata，用于处理“读请求”
2.  有“写请求”到来时，namenode会首先写editlog（数据信息）到磁盘，即向edits文件中写日志，成功返回后，才会修改内存（添加元数据信息metedata），并且向客户端返回
3. Hadoop会维护一个fsimage文件，也就是namenode中metedata的镜像，但是fsimage不会随时与namenode内存中的metedata保持一致（2.0以后实时同步），而是每隔一段时间通过合并edits文件来更新内容。Secondary namenode就是用来合并fsimage和edits文件来更新NameNode的metedata的。

### SecondaryNameNode

​			只有hadoop1.0 和2.0的伪分布式才会有secondryNameNode，secondryNameNode是HA的一个解决方案。但不支持热备。配置即可。

**SecondaryNameNode协助同步Fsimage**

​	从NameNode上下载元数据信息（fsimage,edits），然后把二者合并，生成新的fsimage，在本地保存，并将其推送到NameNode，替换旧的fsimage.默认在安装在NameNode节点上，但这样...不安全！

![image-20220511105252623](Hadoop.assets/image-20220511105252623.png)

1. NameNode  生成Edits.new 文件  与edits 相同，此后dataNode的日志信息会记录在edits.new中
2.  secondaryNameNode 去NameNode取到edits与fsimage
3.  secondaryNameNode 将edits与fsimage在内存中进行合并（元数据信息的合并），生成新的fsimage
4. secondaryNameNode 将 合并完成的fsimage发给NameNode 
5. NameNode 用新的fsimage替换以前的fsimage 完成 fsimage的更新
6. NameNode中的日志文件Edits.new  替换掉原来的Edits ,后续日志信息全部写入Edits中，整个过程结束。

<img src="Hadoop.assets/image-20220511104245550.png" alt="image-20220511104245550" style="zoom: 67%;" />

- 存入DataNode的文件都是以block块的形式存在，block块大小默认为128M，
- NameNode中元数据信息metedata 的数据形式如下：
  - test/a.log, 3 ,{blk_1,blk_2}, [{blk_1:[h0,h1,h3]},{blk_2:[h0,h2,h4]}]
  - 文件名，文件数量，文件块名称，文件块名及对应存储的datanode名字

### DataNode

​		DataNode提供真实文件数据的存储服务。文件块（block）：最基本的存储单位。对于文件内容而言，一个文件的长度大小是size，那么从文件的０偏移开始，按照固定的大小，顺序对文件进行划分并编号，划分好的每一个块称一个Block。HDFS默认Block大小是128MB，以一个256MB文件，共有256/128=2个Block.不同于普通文件系统的是，HDFS中，如果一个文件小于一个数据块的大小，并不占用整个数据块存储空间。Replication。多复本。默认是三个。

### 客户端写入文件流程图

<img src="Hadoop.assets/image-20220511104720901.png" alt="image-20220511104720901" style="zoom:67%;" />

1. 客户端发起写入请求到 NameNode
2. NameNode返回信息到和客户端，告知写入DataNode的位置等信息
3. 客户端将信息写入DataNode，同时，NameNode的Edits日志信息中会保存一条日志信息  （写入成功/失败信息）
4. 若写入成功，NameNode的内存中会生成一条元数据信息meteData
5. 这条写入信息保存在Edits与内存中， checkpoint后，同步至fsimage中  ，同步过程见SecondaryNameNode协助同步Fsimage流程图
6. checkpoint 的时机
   1. fs.checkpoint.period 指定两次checkpoint的最大时间间隔，默认3600秒。 
   2. fs.checkpoint.size    规定edits文件的最大值，一旦超过这个值则强制checkpoint，不管是否到达最大时间间隔。默认大小是64M。

## MapReduce

### MapReduce执行过程

#### 执行MR的命令：

​	hadoop jar <jar在linux的路径> <main方法所在的类的全类名> <参数>

#### MR执行流程

1. 客户端提交一个mr的jar包给JobClient(提交方式：hadoop jar ...)
2. JobClient通过RPC和ResourceManager进行通信，返回一个存放jar包的地址（HDFS）和jobId
3. client将jar包写入到HDFS当中(path = hdfs上的地址 + jobId)
4. 开始提交任务(任务的描述信息，不是jar, 包括jobid，jar存放的位置，配置信息等等)
5. NodeManeger进行初始化任务
6. 读取HDFS上的要处理的文件，开始计算输入分片，每一个分片对应一个Mapper
7. NodeManager通过心跳机制领取任务（任务的描述信息）
8. NodeManager下载所需的jar，配置文件等
9. NodeManager启动一个yran child子进程，用来执行具体的任务（Mapper或Reducer）
10. 将结果写入到HDFS当中

#### 流程图

![image-20220511105806226](Hadoop.assets/image-20220511105806226.png)