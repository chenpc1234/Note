# Struts2

## 简介

​		apache把webwork收购了。然后以webwork为核心+struts1的一些特新演变成了今天Struts2。Strutst1的核心控制器是一个servlet，Struts2的核心控制器是一个filter

## 配置流程

1. 导入jar文件-----7个

2. 配置web.xml文件添加

   ```xml
   <filter>
       <filter-name>struts2</filter-name>
       <!--注意：不同的struts2的版本中 这个filter的全名会有变化-->
       <filter-class>
           org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter
       </filter-class>
   </filter>
   <filter-mapping>
       <filter-name>struts2</filter-name>
       <url-pattern>/*</url-pattern>
   </filter-mapping>
   
   ```

3. 添加struts.xml配置文件

   ```tex
   在package标签中添加<action>节点
   
   <action name="suibian" class="com.sxt.web.actions.ActionDemo">
   
   #name属性：访问这个action的URI
   #class属性：放问这个URI后执行那个action的全名
   ```

4. 编写action

   ```java
   public class ActionDemo {
   	public String execute(){
   		System.out.println("HelloWorld");
   		return null;
   		}	
   }
   在这个java类中需要含有一个方法名称为execute的方法。因为这个方法是核心控制器默认调用的方法。
   ```

5. 给客户端产生响应：

   ```tex
   通过execute方法的返回值来决定返回到什么位置。
   在struts.xml文件中通过<result>节点来配置响应的配置信息
   <action name="suibian" class="com.sxt.web.actions.ActionDemo">
       		<result name="abc">/ok.jsp</result>
   </action>
   result节点中的name属性的值就是execute方法返回的那个字符串。如果对应上了。那么就会返回<result>节点中所夹含路径的页面
   ```

6. 在struts2中获取用户提交的表单数据

   ```tex
   在struts2中为我们提供了一套标签库,在jsp页面中如果使用struts2的标签库，那么需要在页面头部使用指令表加以声明：
   <%@ taglib   prefix="s" uri="/struts-tags" %>
   如果页面中引入了这个标签，那么html部分的内容就可以使用struts2标签库下的标签来编写
   
   <s:form action="" method="" >
       <s:textfield name="" label="用户名"></s:textfield>
       <s:password name="" label="密码"></s:password>
       <s:submit value="OKOK"></s:submit>
   </s:form>
   注意：如果使用struts2的标签库来完成页面的编写，那么他会默认的给定一个主题。如果我们不需要这套
   默认的样式可以在<s:form标签中添加属性theme="simple"来取消默认的样式。
   取消后，页面的编写就和以前是一样的了。
   
   <s:form action="" method="" theme="simple">
       用户名<s:textfield name="" label="用户名"></s:textfield>
       密码<s:password name="" label=""></s:password>
       	<s:submit value="OKOK"></s:submit>
   </s:form>
   
   在struts2中如何获取页面提交的表单数据：
   
   (1)属性驱动式：
   public class RegistAction {
       private String userName;
       private String userPwd;
       get/set
   }
   此时struts2的控件。会帮助我们将页面中的数据放入到这个对象下的属性中，放入的过程是通过调用改属性的
   set方法来完成的。
   注意：要求页面中的name值，必须要和对象中的属性的名称相同。
   (2)对象驱动式：
   Action:
   public class RegistAction {
       private Users user;
       public Users getUser() {
       return user;
   }
   public void setUser(Users user) {
   	this.user = user;
   }
   实体类：
   public class Users{
       private String userName;
       private String userPwd;
       get/set...
   }
   页面中：<input type="text" name="user.userName"/>
   要求页面中的表单的name值必须由action下的属性名称.对象下的属性的名称 
   ```

7. 在struts2中实现服务端数据校验

   ```tex
   a.需要继承一个ActionSupport
   b.重写validate方法。在这个方法中我们主要完成不合法的规则的给定。
   c.如果满足不合法的条件了。那么需要调用超类this.addFieldError("key","value");
   d.validate方式并不需要我们来调用。完全是有struts2控制器来自动调用的。
   如果没有编写服务端的数据校验，那么他会调用超类中的validate。而超类validate方法为空。//多态
   
   e.一单出现数据不合法的现象。那么控制器会去struts.xml中的这个action配置节点下查找一个name叫
   input的result。resutl节点中配置的就是需要跳转页面。
   
   在校验的过程中我们有两种机制来显示错误信息，
   1.跳转一个error.jsp中
   2.那来回那。
   无论哪种跳转方式。在页面中获取错误信息的机制都是相同的。
   <s:fielderror></s:fielderror>//显示全部错误信息
   
   //分开显示。<s:param>key</s:param>
   <s:fielderror>
   <s:param>unameisnull</s:param>
   </s:fielderror>
   ......................
   <s:fielderror>
   <s:param>upwdisnull</s:param>
   </s:fielderror>
   ```

## 拦截器

​		拦截器类似于Servlet中的filter介于请求与action之间。可以拦截对action的访问，有助于实现模块化的程序设计，实现类似“拔插式”的设计，降低模块之间的耦合度。 拦截器在执行的过程中，他的执行顺序并不是取决于在struts.xml文件中声明的顺序，而却取决于配置的顺序。

### Action拦截器

**注意事项：** 

- 当我们为某个action配置了拦截器，那么默认的拦截器将不会为你服务

- 如果你的拦截器是拦截当前package下所有的action，那么你也可以在package中添加
  		<default-interceptor-ref name="my3"></default-interceptor-ref>

- 如果只想拦截个别的action，这个时候需要在这个action中单独配置。并且把默认的拦截器defaultStack也配置进去，否则很多功能没法用

- 编写拦截器：action拦截器继承Interceptor

  ```tex
  public class MyInterceptor1 implements Interceptor {
  	public void destroy() {
  		System.out.println("MyInterceprot1......destroy");
  	}
  	public void init() {
  	    System.out.println("MyInterceprot1......init");
  	}
  	public String intercept(ActionInvocation arg0) throws Exception {		
  		System.out.println("MyInter1......begin");
  		String str = arg0.invoke();
  		System.out.println("MyInter1......End");
  		return str;
  	}
  }
  ```

- 配置拦截器:

  ```xml
  - <package name="default" namespace="/" extends="struts-default">
    	    <!-- 声明拦截器 -->
    	    <interceptors>
        		<interceptor name="myinter1" class="com.sxt.interceptors.MyInterceptor1"></interceptor>
        		<interceptor name="myinter2" class="com.sxt.interceptors.MyInterceptor2"></interceptor>
        		<interceptor name="myinter3" class="com.sxt.interceptors.MyInterceptor3"></interceptor>
        	<!-- 可以将拦截器分组 -->
        		<interceptor-stack name="my1">
        		    <interceptor-ref name="myinter1"></interceptor-ref>
        		</interceptor-stack>
  
      		<interceptor-stack name="my2">
      		    <interceptor-ref name="myinter2"></interceptor-ref>
      		    <interceptor-ref name="my1"></interceptor-ref>
      		</interceptor-stack>
      
      		<interceptor-stack name="my3">
      			<interceptor-ref name="my2"></interceptor-ref>
      		    	<interceptor-ref name="myinter3"></interceptor-ref>
      		   	<interceptor-ref name="defaultStack"></interceptor-ref>
      		</interceptor-stack>
      
      	</interceptors>
      	<action name="demoAction" class="com.sxt.web.actions.DemoAction">
      	<!-- 为action配置拦截器 -->
      		<interceptor-ref name="my3"></interceptor-ref>
      	</action>
  
     </package>
  ```

### 方法拦截器

1. 继承MethodFilterInterceptor

   ```java
   public class MethodIntercept extends MethodFilterInterceptor {
   	protected String doIntercept(ActionInvocation arg0) throws Exception {
   		System.out.println("MethodInter1.......begin");
   		String str = arg0.invoke();
   		System.out.println("MethodInter1.......end");
   		return str;
   	}
   }
   ```

2. 配置struts.xml

   ```xml
    <package name="default" namespace="/" extends="struts-default">
       	<interceptors>
       		<interceptor name="myMethodInter" class="com.sxt.interceptors.MethodIntercept"></interceptor>
       	</interceptors>
       	<action name="methodInterDemoAction" class="com.sxt.web.action.MethodInterDemoAction">
       		<interceptor-ref name="myMethodInter">
       				<param name="includeMethods">executeA,executeB</param> //需要拦截的方法
       				<param name="excludeMethods">executeC</param>//不需要拦截的方法
       		</interceptor-ref>
       	</action>
   </package>
   ```

注意：在配置方法拦截器时，如果将这个拦截器配置到了拦截器栈中，那么对于拦截方法的指定也需要在栈中配置。

## 类型转换

1. 编写一个类型转换器：继承DefaultTypeConverter重写converValue方法

```java
public class SubConversion extends DefaultTypeConverter {
	public Object convertValue(Map<String, Object> context, Object value,
			Class toType) {
		//jsp----->aciton的类型转换 例子：string转一个subjection对象
		//toType 表示需要转换成某个类型的一个类型
		//value 表示得到的值
		//return  转换完的
		if(toType == Subjection.class){
			System.out.println(value);
			String[] str = (String[])value;
			//数学：90
			String[] val = str[0].split(":");
			Subjection sub = new Subjection();
			sub.setSubJectionName(val[0]);
			sub.setSubJecdtionScore(Double.parseDouble(val[1]));
			return sub;
		}
		//action---》jsp  例子：subjection对象---》string
		if(toType == java.lang.String.class){
			Subjection sub = (Subjection)value;
			StringBuffer sb = new StringBuffer();
			sb.append(sub.getSubJectionName()).append(":").append(sub.getSubJecdtionScore());
			return sb.toString();
		}
		return super.convertValue(context, value, toType);
	}	
}
```

2. 编写一个properties文件,名字：要用转换器的action名字-conversion.properties

   1. 用action的名称作为他的前缀: 中间添加-conversion.properties

   2. 例如：UserDemoAction.aciton

      UserDemoAction-conversion.properties

      内容：在properties文件中添加你需要转的那个对象的名称，这个名称作为文件的key，value为自定义转换器的全名
      	sub=com.sxt.conversions.SubConversion

## 国际化

1. 先准备国际化资源文件

   1. suibian_en_US.properties
   2. suibian_zh_CN.properties

2. 修改struts.xml文件

   ```java
   <struts>
       <!-- 加载国际化资源文件 -->
   <constant name="struts.custom.i18n.resources" value="suibian" />//value的值必须和准备的资源文件前部名字一致 参考这个例子
   <package name="default" namespace="/" extends="struts-default">
       <action name="userOperAction" class="com.sxt.web.action.UserOperAction">
       	<result name="success">/showUser.jsp</result>
       </action>
   </package>
   </struts>
   ```

3. 修改页面

   ```jsp
   <s:form action="userOperAction.action" method="post" theme="simple">
   		//msg 为资源文件里边的key 输出对应的value
   		<s:text name="msg1"></s:text><s:textfield  name="user.userName"></s:textfield><br/>
   		<s:text name="msg2"></s:text><s:password name="user.userPwd"></s:password><br/>
   		<s:text name="msg3"></s:text><s:textfield  name="user.userBirth"></s:textfield><br/>
   		<s:text name="msg4"></s:text><s:textfield  name="user.sub">(<s:text name="msg5"></s:text>)</s:textfield><br/>
   		<s:submit key="msg6"></s:submit>
   </s:form>
   ```

## 多分发

​			在一个action中能够处理多个请求。

- 在Servlet中实现多分发的时候，我们是才用域名后加？flag=方法名；例如

```java
//action="OperatorUser.do?flag=insert";
//action="OperatorUser.do?flag=delete"
    
public class OperatorUser {
	doPost(){
		String flag = request.getParameter("flag");
		if("insert".eq(flag)){	this.addUser();}
		else if("delete".eq(flag)){this.deleteUser();}  }
	private void addUser(){}
	private void deleteUser(){}
}
```

- 在struts2中方式1：

  1. 页面中引用不同的action

     ```jsp
     <a href="addUser.action">添加用户</a><br/>
     <a href="updateUser.action">修改用户</a><br/>
     ```

  2. 编写action时 继承ActionSupport

     ```java
     public class OperatorUserAction extends ActionSupport{
     	public String insertUser(){	return null;	}
         public String deleteUser(){ return null;	} 
     }
     ```

  3. 配置struts.xml时, 让页面中的多个action指向自己写的集中处理action类并且标明method

     ```xml
     <action name="addUser" class="com.sxt.web.actions.OperatorUserAction" method="insertUser"></action>
     <action name="findUser" class="com.sxt.web.actions.OperatorUserAction" method="findUser"></action>
     ```

  4. 这样做会让页面中的连接指向对应的action的class 在class 里 通过method决定执行哪个方法,这种多分发的机制采用的是在配置文件中，通过action节点中的method属性来完成方法的定位。

- 在struts2中方式2：

  1. 页面中引用相同的Action 但是在action后变添加！方法名

     ```jsp
     <a href="operatorUserAction!insertUser.action">添加用户</a><br/>
     <a href="operatorUserAction!updateUser.action">修改用户</a><br/>
     ```

  2. 编写action时 继承ActionSupport   和方式1相同

     ```java
     public class OperatorUserAction extends ActionSupport{
     	public String insertUser(){	return null;	}
         public String deleteUser(){ return null;	} 
     }
     ```

  3. 配置struts.xml时，和不分发的时候配置方式相同。不用配置多余的信息

     ```xml
     <action name="operatorUserAction" class="com.sxt.web.actions.OperatorUserAction"></action>
     ```

- 
  分析优劣：方式1的xml配置太多，不方便。多数情况下用方式2，方式2注意 叹号后的方法名字和action里的要一致

## action与jsp间值的传递

### jsp到action

- 前提：jsp中引用struts2的jsp标签库<%@ taglib prefix="s" uri="/struts-tags"%> 使用structs提供的标签语言

- 属性驱动：

  1. jsp中

     ```jsp
     <s:textfield name="abc"></s:textfield>
     ```

  2. 在action中获取这个name对应的值，可以创建一个属性（成员变量）abc; 并生成get、set方法

  3. 在触发这个action时，struts2 会把值放入这个属性里。

  4. 在execute()方法或者自己多分发的方法里就可以用这个属性了。

  5. 注意：jsp标签的name的值 Abc  要和action内的变量名字Abc完全一致

- 对象驱动：更智能的方式

  1. jsp中：

     ```jsp
     <s:textfield name="u.age"></s:textfield>
     ```

  2. 创建的User实体类(含有属性的get、set)。

  3. 想在action中获取这个name对应的值。可以创建一个属性（成员变量） private User u;并生成get、set方法

  4. 在触发这个action时，struts2会把值封装这个user对象里。

  5. 在execute()方法或者自己多分发的方法里就可以随便用这个User了。

  6. 注意：u.age   其中 u要和action内User类型的属性 u的名字保持一致。age要和实体类的属性名字完全一致

### action到jsp

- El表达式可以获取

- 写对名字 直接获取

- 例如返回的是一些user对象   用list存的

  ```jsp
  <s:if test="list != null && list.size() > 0">
      		<tr>
      		    <th>编号</th>
  	    	  	<th>用户名</th>
  	    		<th>性别</th>
  	    		<th>爱好</th>
  	    		<th>民族</th>
  	    		<th>介绍</th>
  	    		<th>操作</th>
  	        </tr>
      	<s:iterator value="list" status="s">
      	<tr>
      		<td><s:property value="#s.count"/></td>
      		<td><s:property value="userName"/></td>
      		<td>
      			<s:if test="userSex == 1">
      				男
      			</s:if>
      			<s:else>
      				女
      			</s:else>
      		</td>
      		<td><s:property value="userLike"/></td>
      		<td><s:property value="userMinZu"/></td>
      		<td><s:property value="userDesc"/></td>
      		<td>
      		<a href="operatorUserAction!preUpdateUser.action?user.userName=<s:property value="userName"/>">更新</a>&nbsp;&nbsp;
      		<a href="operatorUserAction!dropUser.action?user.userName=<s:property value="userName"/>">删除</a>
      		</td>
      	</tr>	
      	</s:iterator>
  </s:if>
  <s:else>
      <tr>
          <td>空</td>
      </tr>
  </s:else>
  ```

## 获取常用对象

​		在struts2中如果获取servlet中的常用对象。request，httpsession，servletcontext主要有两种获取机制：

- 通过实现接口机制来完成对象的获取。这个过程我们称之为：注入。

  ```java
  public class UserAction extends ActionSupport implements RequestAware,SessionAware,ServletContextAware {
  	private Map<String, Object> request ;
  	private Map<String, Object> session;
  	private ServletContext servletContext;
  	public String execute() throws Exception {
  		request.put("msg", "你干啥？");
  		session.put("user", user);
  		this.servletContext.setAttribute("context", "是我是我！还是我！");
  		return SUCCESS;
  	}
  	public void setRequest(Map<String, Object> request) {
  		this.request = request;	
  	}
  	public void setSession(Map<String, Object> session) {
  		this.session = session;
  	}
  	public void setServletContext(ServletContext context) {
  		this.servletContext = context;
  	}
  }
  ```

- 可以通过工具类来获取

  ```java
  public class UserAction2 extends ActionSupport {
  	public String execute() throws Exception {
  		HttpServletRequest request = ServletActionContext.getRequest();
  		HttpServletResponse response = ServletActionContext.getResponse();
  		HttpSession session = ServletActionContext.getRequest().getSession();
  		ServletContext sc = ServletActionContext.getServletContext();
  		return this.SUCCESS;
  	}
  }
  ```

## 响应类型

​			struts.xml文件中的响应类型： 默认为dispatcher

```xml
<result name="ok" type="???????">/???</result>
```

1. redirect:	重定向的方式来给客户端产生一个响应

2. dispatcher:	请求转发

3. stream:	以流的形式响应

4. redirectAction:	重定向到一个aciton

   1. 注意：由于是使用重定向的跳转，那么在传值的过程中。我们只能通过URL后来进行值的传递,如果传递的信息过大，有中午且容易出现乱码。那么这种方式是不合适的。

      ```xml
      <result name="ok" type="redirectAction">/findLevelProductAction!findLevelProduct.action?userName2=${userName}&amp;level2=${level}</result>
      ```

5. chain:	链式调转，有服务器直接发送请求，不经过客户端。

   注意：当请求第二个aciton时，在这个action中如果含有和上一个action中同名的属性，那么这个action中的属性
   会将其覆盖点，我们可以在这个action所调转的页面中直接获取到上个action中的属性的值。但是前提这个action中的跳转到页面的方式不能是redirect跳转。

## 文件上传与下载

### 上传

​		Struts2在实现文件上传时会自动的将需要上传的文件封装成File对象。可以直接通过FileInputstream流对象将上传的这个文件读入，在保存这个文件时，Struts2帮助我们将这个文件的文件名直接提取出来了，放到了一个名字格式固定属性中，这个名字的构成是由页面中的file标签的name的名称+关键字FileName
案例：

jsp：

```jsp
	<file name="file">
```


action中：

```java
public class FileUploadAction ex ActionSupport{
		private String fileFileName //这就是那个文件名。   fileFileName 就是由jsp中的name+FileName 构成
}
```

​		Struts2默认的上传文件大小 限定为2M多点,如果上传的图片超过了这个范围，那么我们需要在struts.xml文件中做配置

```xml
<constant name="struts.multipart.maxSize" value="10000000" />  
//定义在package之外因为是全局公用的value的值就是制定上传文件大小
```

### 下载

​	Struts.xml文件中，配置下载的action时，响应的result节点会有变化，响应类型 type="stream"

```xml
<action name="fileDwonAction" class="com.sxt.web.actions.FileDownAction">
        <!--type需要是stream类型-->
    	<result name="success" type="stream">
		<!--设置响应类型-->
		<param name="contentType">bin</param>  //bin jpg 等  参考文件下载里边的类型
		<!--设置下载文件名-->
		<param name="contentDisposition">attachment;filename=${tpName}</param><!-- 下载时的文件名,attachment防止文件打开 -->
		<!--获取下载的流对象。downFile是action中的一个方法，这个方法能够返回一个流对象-->
		<param name="inputName">downFile</param>
		<!--下载文件时的字节缓冲区-->
		<param name="bufferSize">4096</param>
    	</result>
</action>
```



action中

```java
public class FileDownAction extends ActionSupport {
	private String tpName;
	public String getTpName() {
		return tpName;
	}
	public void setTpName(String tpName) {
		this.tpName = tpName;
	}
	public String fileDown(){	
		return this.SUCCESS;
	}
    
    	//记住这个方法的作用      jsp中：<a href="fileDwonAction!fileDown.action?tpName=1445566268389.GIF">下载1445566268389.GIF</a>
    public InputStream getDownFile(){
	return ServletActionContext.getServletContext().getResourceAsStream("/fileupload/"+this.tpName);
	}
}
```

​		在struts2文件下载时或者在页面中点击取消按钮那么程序会抛出一个异常，这个异常本身并不会影响程序运行期，产生的原因是struts2的在IO时关闭流中出现了错误。如果需要修复这个问题，我们可以下载其他的jar包来修复。修改struts.xml并将action中响应类型配置为type=streamx就可以了。

```xml
<result-types>
	<result-type name="streamx" class="com.sunspoter.lib.web.struts2.dispatcher.StreamResultX">
    </result-type>
</result-types>
```

## 注解相关

### @Action

- @Action  可以配置在类前 ,也可以配置在方法前

- @Action内的名字不能重复。因为他相当于xml中action的name

- 配置在类名前

  ```java
  @Action("hello")
  public class ActionDemo extends ActionSupport{}
  ```

  这种相当于在struts.xml中如下配置

  ```xml
  <action name="hello" class="com.sxt.web.actions.ActionDemo"></action>
  ```

- 配置在方法名前

  ```java
  @Action(value="hahaha")
  public String bb()｛return null ;}
  ```

  这种配置相当于在xml中

  ```xml
  <action name="hahaha" class="com.sxt.web.actions.ActionDemo" method="bb"></action>
  ```


  相当于用多分发中的第一种方式 实现多分发

- @Actions(  {@Action("A"),@Action("B"),@Action("C")} )
  使用方法和@Action一样，就是多个连接指向一个action   也就是A，B，C  这三个 都是指向这个方法（或者类）

### @Results

@Results可以放在类名之前,映射结果集

```java
@Results({
	@Result(name="ok",location="/index.jsp",type="redirect"),
	@Result(name="addUser" ,location="addUser.jsp"),
	@Result(name="error",location="/error.jsp"),
	@Result(name="error1",location="/error1.jsp")
})

```

### @ExceptionMappings

异常信息跳转页面

```java
@ExceptionMappings({
	@ExceptionMapping(result="error",exception="NullPointerException"),
	@ExceptionMapping(result="error1",exception="Exception")
})
```

### @InterceptorRefs

​		拦截器可以在类和方法的层面上应用。在方法层面指定拦截器使用@Action注解，在类层面指定拦截器使用@InterceptorRefs注解。类层面引用的拦截器会应用在所有的方法上。

```java
@InterceptorRefs({     
    @InterceptorRef("interceptor-1"),     
    @InterceptorRef("defaultStack") 
}) 
public class HelloWorld extends ActionSupport {   

@Action(value="action1", interceptorRefs=@InterceptorRef("validation"))   
public String execute() {     return SUCCESS;   }    

@Action(value="action2")   
public String doSomething() {     return SUCCESS;   } 
}
```

​		如上代码所示，execute()方法应用了interceptor-1,validation和defaultStack中的所有拦截器；而doSomething()方法则没有validation拦截器。 

## OGNL表达式

​		ognl本身为我们准备了一个能够存放数据的栈，这个栈 valueStack参考资料http://blog.csdn.net/tjcyjd/article/details/6850203
Struts2中OGNL表达式要求必须要和struts2中的标签配合使用。

```jsp
获取非栈根的内容。
<s:property value="#parameters.msg" />    //获取validators 里面param节点里的信息
<s:property value="#request.msg" />    //获取request里的信息
<s:property value="#session.msg" /> 	//获取session里的信息   
<s:property value="#application.msg" />	//获取ActionContext里的信息   
<s:property value="#attr.msg" />//获取page-->request-->session-->application最近的信息
<s:property value="list" />//获取list里的所有对象，自定义的为引用  基本类型为值
过滤：
<s:property value="list.{?#this.属性>(<,==)某个值 }" />//获取符合条件的list里的所有对象，自定义的为引用  基本类型为值
<s:property value="list.{^#this}" />                 //获取list里的第一个对象
<s:property value="list.{$#this}" />                   //获取list里的最后一个对象
<s:property value="persons.{?#this.name=='pla1'}.{age}[0]"/>//获取符合条件的对象的某个属性
<!-- 集合.{?#this.属性判断} -->
<s:iterator value="list.{?#this.userAge>20}">    //迭代器  value=listname.{?#this.filedname>（<,==）？}
     <li><s:property value="userName" /> - 年龄：<s:property value="userAge" </li>    
</s:iterator>  
构造Map：    
<s:set name="foobar" value="#{'foo1':'bar1', 'foo2':'bar2'}" />   
<s:property value="#foobar['foo1']" /></p>    //取name为foobar的Map下 key为foo1 对应的value
```

- %:       类似于js中eval()函数，会把一个字符串当成命令来执行。
- $:      一般用户后去某个值时使用。比如struts.xml文件中 我们通过${}来获取action中的值。
- @:        可以帮助我们去调用某个类下的静态方法或者静态变量。但是在2.1以后的版本中这个功能默认的是禁止。

我们需要在struts.xml文件中通过配置常量类 来开启这个功能

```xml
<constant name="struts.ognl.allowStaticMethodAccess" value="true"></constant>
```

- 解决重复提交步骤：
  1. 在禁止重复提交的表单中添加 *< s:token >*
  2. 在配置文件中开启token拦截器。在默认的拦截器栈中 是不包含token拦截器的，我们需要将他引入进来。注意
     别忘了在讲defaultStack一并引入进来。