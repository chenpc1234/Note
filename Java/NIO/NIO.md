# NIO

## 高并发IO的底层原理

### IO读写的基本原理

为保证系统安全，操作系统将虚拟内存划分为两部分：内核空间和用户空间。Linux中，内核模块运行在内核空间，用户程序运行在用户空间。

- 内核模块
  - 运行在内核空间，对应进程为内核态
  - 有权访问内核空间，有权访问硬件设备
  - 内核态进程可以执行任何命令，调用系统的一切资源
- 用户程序
  - 运行在用户空间，对应进程为用户态
  - 无权访问内核空间（读写），无权访问硬件设备
  - 用户态进程不能访问内核空间，不能调用内核函数，只能进行简单运算。
- IO读写
  - 用户程序进行的IO读写依赖于底层的IO读写。
    - 底层的IO位read 和write两大系统，都涉及到缓冲区。
  - 用户程序的IO读写不是物理设备的读写，是缓存的复制。用户程序IO调用底层IO进行缓存复制
    - read 将数据从内核缓冲区复制到进程缓冲区
    - write 将数据冲进程缓冲区复制到内核缓冲区



### 系统调用流程

<img src="NIO.assets/image-20220525124417754.png" alt="image-20220525124417754" style="zoom:50%;" />

java客户端与服务端之间完成一次socket请求和响应（包含read 和write）的数据交换，流程如下：

1. 客户端发送请求

   1. Java客户端通过write系统将数据复制到内核缓冲区
   2. 客户端 Linux将内核缓冲区的请求数据通过网卡发送出去，到服务端

2. 服务端获取请求

   1. 服务端Linux从网卡接收数据，读取到内核缓冲区
   2. Java服务端程序通过调用read系统，将内核缓冲区数据复制到Java进程缓冲区

3. 服务端业务处理

   ​			服务器在进程空间完成自己的业务处理

4. 服务端返回数据

   ​			服务器处理完成，调用write系统将用户缓冲区的数据写入内核缓冲区

5. 发送给客户端

   ​			操作系统将内核缓冲区的数据写入网卡，网卡通过底层通讯协议传输给客户端

### 四种IO模型

#### 概念

- 阻塞
  - 阻塞是指用户程序的执行状态。在内核IO的执行过程中，发起IO的进程或者线程处于阻塞状态。
- 同步
  - 同步IO是用户空间主动发起IO请求，系统内核被动接收。
- 异步
  - 异步IO是系统内核主动发起IO请求，用户空间是被动接收。

#### 同步阻塞IO

- 概念

  - Blocking IO 由用户空间（或者线程）发起，需要等待内核IO操作彻底完成之后才返回用户空间的IO操作。在内核IO的执行过程中，发起请求的线程或者进程处于阻塞状态。
  - 传统的IO模型都是阻塞IO，java默认创建的socket是阻塞IO

- 流程图

  <img src="NIO.assets/image-20220525153404186.png" alt="image-20220525153404186" style="zoom: 50%;" />

  1. IO发起read系统调用，用户线程进入阻塞状态
  2. Linux内核空间准备数据，内核缓冲区可能没有数据（比如在磁盘，没有收到完整的socket）
  3. Linux内核缓冲区数据准备完成，将内核缓冲区数据复制到用户缓冲区，然后返回结果。
  4. 用户线程接收到内核空间的返回结果，解除阻塞状态。

- 特点

  - 用户线程在内核执行的两个阶段处于阻塞状态。

- 优缺点

  - 优点：开发简单，阻塞期间，用户线程挂起不消耗CPU
  - 缺点： 高并发下，阻塞IO需要大量的线程来维护大量的网络连接，线程切换开销巨大，性能急剧下降。

#### 同步非阻塞IO

- 概念

  - Non-Blocking IO  简称NIO。不是Java NIO ，Java中的NIO（New IO）类库的IO模型是IO多路复用模型。
  - 由用户空间（或者线程）发起，不需要等待内核IO操作彻底完成就能立即返回用户空间的IO操作。在内核IO的执行过程中，发起请求的线程或者进程处于非阻塞状态。
  - 非阻塞IO中socket被设置为 nonblocking模式 

- 流程图

  <img src="NIO.assets/image-20220525154332716.png" alt="image-20220525154332716" style="zoom:50%;" />

  1. 用户线程发起read系统调用，在内核系统没有准备好数据的阶段，IO请求立即返回（调用失败）
  2. 用户线程不断发送read请求，直到内核缓冲区有数据准备完成。此时用户线程处于阻塞状态，等待内核缓冲区数据复制到用户缓冲区。
  3. 内核缓冲区数据复制完成，内核返回调用结果
  4. 用户进程或者线程接收到结果，解除阻塞状态。

- 特点

  ​			用户线程需要不断的进行调用

- 优缺点

  - 优点：实时返回（内核缓冲区数据准备未完成时直接返回失败）线程不会阻塞，实时性较好
  - 缺点： 大量的轮询调用，占用CPU资源，效率低。

#### IO多路复用

- 概念

  - IO Multiplexing是经典的Reactor模式实现，也被成为异步阻塞IO。Java中的Selector 属于这种模型
  - 在Linux系统中，新的系统调用为select/epoll系统调用。通过系统调用，一个用户进程可以监控多个文件描述符，一旦某个描述符就绪（内核缓冲区可读/可写），内核空间能过将文件描述符的就绪状态返回给用户进程，用户空间根据文件描述符文的就绪状态进行IO系统调用

- 流程图

  <img src="NIO.assets/image-20220525155326493.png" alt="image-20220525155326493" style="zoom:50%;" />

  1. 选择器注册：将需要操作的目标文件提前注册到选择器上，然后开启IO多路复用的轮询流程。
  2. 就绪状态轮询：通过选择器的查询方法，查询所有提前注册过的目标文件描述符的就绪状态，内核返回一个就绪的列表。列表中的文件都是在内核缓冲区数据准备就绪的文件。
  3. 用户线程得到就绪列表后，根据列表进行read调用。用户线程阻塞，内核空间进行数据复制到用户空间。
  4. 复制完成后，内核空间返回调用结果，用户线程解除阻塞。

- 特点

  ​		IO多路复用涉及到两种系统调用，一个是select /epoll 的就绪查询系统调用，一个是read/write系统调用。

- 优缺点

  - 优点是： 一个查询选择器可以处理成千上万的网络连接，不必创建大量的线程。
  - 缺点是：本质上select/epoll 是阻塞的属于同步IO。

#### 异步IO

- 概念

  - Asynchronous IO 简称AIO，用户空间的线程或者进程变成被动接收者，内核空间变成主动调用者。
  - 用户空间接收到通知时，内核空间已经完成了数据从内核空间到用户空间的过程，用户线程可以直接使用。类似Java的回调函数。

- 流程图

  <img src="NIO.assets/image-20220525160746753.png" alt="image-20220525160746753" style="zoom:50%;" />

  1. 用户线程发起read请求，可以立即去做其他事情，线程不阻塞
  2. 内核开始准备数据，内核将数据冲内核缓冲区复制到用户缓冲区。
  3. 内核给线程发一个信号，或者回调用户线程的注册的回调方法，告诉read调用完成。
  4. 用户线程读取用户缓冲区的数据，完成后续操作。

- 特点

  - 在内核准备数据及复制数据的两个过程中，用户线程都不阻塞。
  - 异步IO也被成为信号驱动IO，因为用户线程需要接收内核IO操作的完成事件，或者提前注册一个内核IO完成回调函数给内核使用。

- 优缺点

  - 优点： 完全的异步，用户线程不阻塞。
  - 缺点 ： 用户程序仅仅需要进行时间的注册与接收，其他的都交给了内核。需要操作系统的支持，目前JDK支持不完善。

### 配置文件描述符限制

Linux默认文件句柄数是1024 ，即一个进程最多接收1024个socket连接。

- 查看文件句柄数

  ```bash
   ulimit -n
  ```

- 修改文件句柄数

  ```bash
  ulimit -n 100000   #进当前会话有效，重启失效
  ```

- 永久修改：

  ```tex
  修改 /etc/rc.local 添加
  ulimit -SHn 100000
  ```

- 软性极限值与硬性极限值

  ```tex
  修改/etc/security/liomits.conf添加
  soft nofile 10000   # 软性极限
  hard nofile 10000	# 硬性极限
  ```

## Java NIO

### 简介

Java NIO类库包含三大核心组件

- Channel 通道
- Buffer 缓冲区
- Selector 选择器

### NIO-Buffer类

NIO的Buffer本质上是一个内存块，既可以写入数据，也可以从中读取数据。

#### 类关系

Buffer是一个抽象类，有八个子类，如下图：

![image-20220525175900160](NIO.assets/image-20220525175900160.png)

#### 属性

##### capacity（容量）

capacity表示缓冲区的大小，capacity一旦初始化，就不可改变。比如 LongBuffer.allocate(100); 表示创建一个大小为100的Long类型的缓冲区，最多可以放入100个long类型的数据。

##### position（读写位置、偏移量）

- 写模式：position初始位置为0，表示当前写入由0开始。每当一个数据写入缓冲区，position后移一位，最大可写值是limit-1，position=limit表示已经写满。
- 读模式:   当刚进入读模式时，position重置为0，读取数据时，position后移，position=limit表示无待读取数据。

##### limit（读写限制）

- 写模式： limit表示数据可以写入的最大上限，刚进入写模式时 limit =capacity
- 读模式： limit表示数据可以读取的最大上限。

##### mark（标记）

在缓冲区操作时，可以将position暂时保存到mark中，需要的时候再从mark取出标记位置，恢复到position属性中，重新从position位置开始处理。

#### 方法

| 方法名                 | 方法作用          | 说明                                                         |
| ---------------------- | ----------------- | ------------------------------------------------------------ |
| allocate(int capacity) | 获取缓冲区实例    | 默认为写模式capacity = capacity ;  position=0; limit =capacity ; |
| put(T t)               | 写入缓冲区        | 写入对象与创建缓冲区类型一致；写入n个后  position =n  ;capacity 、 limit 不变 |
| flip()                 | 写切读            | 写切读： limit = position;   position =0 ;                   |
| get()                  | 读取数据          | 读取position标记处的数据 且position ++                       |
| rewind()               | 读取重置          | position 置0，重新读取                                       |
| mark()                 | 标记              | mark  = position                                             |
| reset()                | 重新设置          | position  = mark                                             |
| clear()                | 清空缓冲区,写切读 | position = 0; limit = capacity; mark = -1;                   |

#### 使用流程

1. 获取Buffer缓冲区实例 →allocate
2. 写入数据 →put
3. 写切读→ flip
4. 读取数据→get
5. 清空缓冲区→clear

### NIO-Channel类

Java NIO一个socket连接使用一个channel表示，Java NIO中有很多通道实现。

![image-20220527150412925](NIO.assets/image-20220527150412925.png)

#### FileChannel

##### 简介

FileChannel是操作文件的通道，是阻塞模式。

##### 使用

- 获取通道

  ```java
  //通过文件输入流、输出流获取FileChannel
  FileInputStream fileInputStream = new FileInputStream("1.txt");
  FileChannel channel = fileInputStream.getChannel();
  
  FileOutputStream fileOutputStream = new FileOutputStream("1.txt");
  FileChannel outputStreamChannel = fileOutputStream.getChannel();
  ```

- 读取通道内数据

  ```java
  //使用read 进行读取,读取到ByteBuffer缓冲区中
  ByteBuffer byteBuffer = ByteBuffer.allocate(100);
  channel.read(byteBuffer);
  // 对于通道来说是读模式，对于缓冲区来说是写模式
  ```

- 数据写入通道

  ```java
  // 缓冲区必须是读模式，使用channel.write()将缓冲区的数据写入通道
  byteBuffer.flip();
  channel.write(byteBuffer);
  ```

- 关闭通道  close()

- 强制刷新到磁盘  force()

##### 案例



#### SocketChannel&&ServerSocketChannel

##### 简介

NIO中的SocketChannel对应OIO的Socket，负责数据传输；  ServerSocketChannel对应ServerSocket 负责连接的监听。都支持阻塞模式和非阻塞模式。

阻塞模式下，SocketChannel的连接、读、写 都是阻塞的，和Socket效率相同。

非阻塞模式下，SocketChannel是异步、高效的。SocketChannel.configureBlocking(false) 可设置为非阻塞模式。

##### 使用

- 获取通道

  ```java
  
  ```

  

- 读取通道内的数据

- 数据写入通道

- 关闭通道

#### DatagramChannel



### NIO-Selector