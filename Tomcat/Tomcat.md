# Tomcat

## 目录结构

- bin：存放了一些bat 和sh文件，重要的有3个
  - startup       运行tomcat 
  - shutdown  结束 
  - catalina   手动不能运行可以在cmd下运行，后面跟start开启 stop关闭
    - 示例  >bin  catalina.bat start 等同于手动运行startup一样了
- conf：存放了xml文件 比如Servlet.xml
- lib：各种jar包，tomcat自身的jar; 实现javaEE平台下部分标准的实现类(比如：jsp  servlet...)
- log：日志文件
- temp：临时文件
- webapps：存放需要让tomcat去管理的资源的目录。
- work：这个目录主要存放的是tomcat对jsp编译完后得原文件以及class文件

## 架构

### tomcat的架构

​	访问服务器的资源的格式：协议:地址:端口/资源目录/资源名称

### tomcat部署项目三种方式：

1. 放到webapps目录下：
2. 修改server.xm 的host节点。添加<Context  path="/访问资源目录" docBase="e:/xxx"/>
3. 在conf目录下找到或者是新建文件夹Catalina---->localhost----->访问资源的   xml-----><Context....../>

- 更改域名：
  - 更改cofn下的Servlet.xml 添加 <host name="域名A" appbase="默认目录A">  此时输入http://域名A:端口号  进入的是你的默认目录A 下的ROOT下的index.html
- 更改资源名
  - 方式1:  将资源放入对应的 默认目录A 输入**http://域名A:端口号/welcome.html** 为root目录下的welcome.html 输入http://域名A:端口号/test/welcome.html 则是test目录下的,test 和root是同目录的 都在默认目录A下 
  - 方式2：更改cofn下的Servlet.xml 在对应的host标签内添加 <Context  path="/访问索引A" docBase="文件物理路径A"/> 此时输入**http://域名A:端口号/访问索引A/**  进入的是你的文件物理路径A文件夹下的index.html 假如访问a文件夹下b文件夹内的C.html  则输入http://域名A:端口号/访问索引A/b/C.html
  - 方式3;在conf目录下找到或者是新建文件夹Catalina---->域名A----->访问索引.xml 内容如下：<Context  path="/访问索引A" docBase="文件物理路径A"/>

## 远程调试

- 如果要调试远程Linux下tomcat应用，配置tomcat在catalina.sh中添加

  ```sh
  CATALINA_OPTS="-Xdebug  -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
  ```

- 如果要远程调试Window下tomcat应用，修改catalina,bat文件，添加：

  ```sh
  Set  “CATALINA_OPTS=-Xdebug  -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
  ```

## idea中tomcat 乱码问题的解决及相关设置

1. Idea中，在tomcat Server中设置 VM options , 值为 -Dfile.encoding=UTF-8

2. Idea中，在setting中的 File encodings 中设置编码格式，是设置页面编码格式的

3. Idea中，在java Complier中设置Additional command line parameters的值，-encoding=UTF-8

4. 在Idea安装目录下bin目录中修改文件idea.exe.vmoptions和idea64.exe.vmoptions中的参数，同时增加-Dfile.encoding=UTF-8

5. 在tomcat 安装路径 bin目录下的catalina.bat文件中加入 -Dfile.encoding=UTF-8

6. 在 tomcat 安装路径 conf 目录下，设置 logging.properties ，增加参数  java.util.logging.ConsoleHandler.encoding = UTF-8

7. 另外在服务器上tomcat还需要设置 server.xml中的参数，以防页面出现乱码

   ```xml
   <Connector port="8080" protocol="HTTP/1.1" connectionTimeout="20000"  redirectPort="8443"  URIEncoding="UTF-8" />
   <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" URIEncoding="UTF-8" />
   
   ```