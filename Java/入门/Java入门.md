# Java入门

## 原码、反码、补码

​	计算机在底层存储数据的时候，一律存储的是“二进制的补码形式”

- 正数的原码、反码、补码都是一样的，首位是符号位
  - 5的原码、反码、补码： 0000 0101
- 负数的原码是对应正数符号位改为1，反码为正数的反码取反，补码为反码取反+1
  - -5原码     	10000101
  - -5的反码     11111010
  - -5的补码     11111011

## 类加载顺序

​	TestStatic t=new TestStatic();

1. 先加载父类然后加载子类到内存

2. 查看有没有静态内容

   1. 如果有加载静态属性
   2. 如果有静态代码块，就执行静态代码块(先父类,后子类)

3. 给对象开辟内存空间

4. 加载对象相关属性，赋默认值

5. 给属性显示初始化

6. 先父类代码块 -->父类构造方法-->子类代码块--->子类构造方法

   ​		子类构造方法 super(); →父类构造方法没执行→先父类代码块→父类构造方法→回到子类构造方法
   →代码块 →如果子类构造方法中有其他代码，再执行其他代码 。

7. 把地址赋给 t

## Servlet

​		tomcat目录下的lib下servlet的jar包内 有servlet.class 这是一个接口，GenericServlet是一个抽象类,实现了servlet接口。
HttpServlet继承了GenericServlet,这个类与Http协议挂钩，降低自己写的方法与Servlet之间的耦合度。

### Servlet的生命周期

1. Servlet的生命周期是由容器管理的
2. 他的生命周期经过三个阶段：初始化 服务 销毁
3. 当第一个客户端浏览器访问这个Servlet时，容器会将这个Servlet实例化并且调用一次init方法，在新的线程中调用service方法，然后容器不会立即销毁这个对象，当容器关闭的过程中，会销毁这个Servlet（通过调用destroy方法) 如果销毁这个对象之前，另一个客户端访问这个Servlet，不会再次实例化，直接在新的线程中调用service方法。

### 解决Servlet中文乱码

**Get方式提交：**

1. get方式提交表单时，如果表单中含有中文，按浏览器的编码方式编码(也就是URI编码)方式，这种编码方式是把中文转化为 %--%--空格变为16进制数，这个编码方式，在提交时候地址栏有显示。

2. 对于转化完成的数据，进行字符转字节处理，然后发送到服务器。字符转字节的编码方式为html内<meta>标签内指定的编码方式编码，一般使用utf-8，如果不指定，按照你html保存在电脑上的编码方式，建议写html时写上meta标签。

3. ---------发送至服务器后----

4. 服务器解码默认使用iso-8859-1 解码方式解码，此时我们要做出修改，改成utf-8方式解码 

   1. 修改方式：就是在doget()方法内添加request.setCharacterEncoding("utf-8"); 这样做，服务器就不会再用iso-8859-1解码了，而是按照我门指定的方式解码

5. tomcat 服务器默认情况下不开启URI解码 ，手动去配置下tomcat下 conf下的Servlet.xml

   ```tex
    <Connector port="8080" protocol="HTTP/1.1" maxThreads="150" connectionTimeout="20000" redirectPort="8443" />  
    
    //这个标签内添加一个URIEncoding="utf-8" 
   
   ```

**Post方式提交：**
	同Get方式，但是post方式只有2和3步骤。

**响应时乱码问题：**
	response.setContentType("text/html");更改为response.setContentType("text/html;charset=utf-8");

### 请求与反馈

1. 获取浏览器附加信息   key：value

   ```tex
   request.getHeader("key");返回对应名字的值  也就是对应key的value 返回类型为String
   request.getHeaderName();返回所有浏览器附加信息的名字：
   key  返回类型为枚举类型   Enumeration<string> enums=request.getHeaderName()
   ```

2. 在响应中添加附加信息：

   ```tex
   在响应中添加信息的方法是 Response.addHeader();
   一般情况也就加个response.addHeader("Content-Disposition", "attachment;filename=\""+file.getName()+"\""); 
   ```

3. 文件上传

   ```tex
   对于文件上传 页面表单的提交方式必须为post  原因是：get方式永远是已字符方式提交，而post既可以已字符也可以已字节。
   <form action="servlet" method="post" enctype="mulitpart/form-data">
   备注： 就算这样处理了，我们得到的上传过来的文件也不正确，因为浏览器在生成数据包的时候，会在上传的首部和尾部添加一些信息，所以以后会使用fileupload组件实现  
   ```

4. 给客户端产生一个响应

   ```tex
   Response.setContentType("text/html");
   这个是默认的，表示以文本Html相应给客户端浏览器。
   Response.setContentType("Type");
   问题：如何不让浏览器打开，点击后返回来的是让自己下载。
   	1.把type设置为bin
   	2.添加：response.addHeader("Content-Disposition", "attachment;filename=\""+file.getName()+"\"");//如果不添加，那么浏览器可以读取到数据，但是他不知道是什么类型的
   ```

### 重定向

​				重定向的响应是建立在两次请求两次响应中完成的，Response.sendRedirect("url");
  **重定向过程：** 

1. 客户端发出请求
2. 服务器收到请求，完成响应，在发送给客户端的响应中添加location：url 
3. 客户端浏览器会立即向这个目标发送请求。
4. 这个url对应的服务器做出响应。
5. 注意的是 **2次请求都是在客户端，响应在不同的服务器**   

### 监听Listener

- 如何编写一个监听器:

  1. 编写一个类实现监听器接口(根据自己的需要来决定实现哪个监听器接口)。

  2. 修改web.xml文件，在配置文件中添加

     ```xml
     <listener>
     	<listener-class>com.sxt.listener.ServletContextListenerDemo</listener-class>
     </listener>
     ```

  3. 实现接口中的抽象方法，决定你使用监听器的功能。

     - 监听servlet上下文对象生命周期的监听器:

       实现ServletContextListener接口

     - 监听HttpSession对象的声明周期的监听器
       实现HttpSessionListener接口

     - 监听HttpServletRequest对象的声明周期
       实现ServletRequestListener接口

     - 监听ServletContext属性操作的监听器
       实现ServletContextAttributeListener接口

     - 监听HttpSession属性操作的监听器
       实现HttpSessionAttributeListener接口

     - 监听ServletRequest属性操作监听器
       实现ServletRequestAttributeListener接口

## Filter过滤器

### **编写配置过滤器**

1. 编写一个class 实现一个javax.servlet.Filter 接口

   ```java
   public class EncodingFilter implements Filter {
   	private String encode = "utf-8";
   	public void destroy() {
   	}
   	public void doFilter(ServletRequest request, ServletResponse response,FilterChain arg2) throws IOException, ServletException {
   		request.setCharacterEncoding(this.encode);
   		arg2.doFilter(request, response);
   	}
   	public void init(FilterConfig config) throws ServletException {
   		String encode = config.getInitParameter("Encoding");//需要配置Config
   		if(encode != null && encode.length() > 0){
   			this.encode = encode;
   		}}}
   ```

2. 在web.xml文件中做配置

   ```xml
   <filter>
   	<filter-name>Encode</filter-name>
   	<filter-class>com.sxt.web.filter.EncodingFilter</filter-class>
   	<init-param>
   		<param-name>Encoding</param-name>
   		<param-value>utf-8</param-value>
   	</init-param>	
   </filter>
   <!--这个节点可以有多个。如果过滤器需要拦截不同的内容，可以通过配置都个mapping来实现-->
   <filter-mapping>
   	<filter-name>Encode</filter-name>
   	<url-pattern>*.do</url-pattern>
   </filter-mapping>
   ```

### **有多个filter执行先后顺序是什么？**

1. 带有星号的优先级高于绝对的。
2. 如果都是是带有星号的(不分绝对通配或者相对通配)按照在配置文件中配置的上下顺序来决定的

### Filter的生命周期：

​	容器在启动时会立即创建filter对象。同时调用一次init方法，当容器关闭时先会调用一次destroy方法然后销毁这个对象。

- init方法：
  - 在这个方法中。有一个入参FitlerConfig这个对象的作用和ServletConfig的作用比较相似都是用来读取配置文件
    中节点信息的。
  - 不同的是 servletConfig只能读取<servlet>下的<init-param>的信息，而FilterConfig只能读取
    <Filter>---<init-param>信息。

