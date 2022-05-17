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



## XML文件

Extensible markup language（可扩展标记语言）
由若干元素组成
例如：xml中<servlet>   称为节点或者元素（element）

```xml
<?xml version="1.0" encoding="utf=8"?>
<!DOCTYPE usersdata SYSTEM "aa.dtd">
<usersdata>
	<user id="zhangsan">
		<username>zhangsan
		This is some arbitrary text <within> a 
		</username>
		<userage>20</userage>
		<usersex>男</usersex>
		<userpwd>admin</userpwd>
		<userbirth>1998-05-01</userbirth>
	</user>
</usersdata>
```

### Xml约束文件

约束文件决定了，当前这个xml文件中可以命令含有什么样的元素，属性，以及内容。

DTD

```tex
表示数量：
	*：0~无穷
	+：1~无穷
	？：0 or 1
CDATA： 
			任意字符+任意符号。？？
#PCDATA
			表示标签内可以有数据 也可以没有数据
<!ELEMENT XXX (AAA,BBB)>
			在xxx这个元素下只能含有AAA元素1次和BBB这个元素一次，并且顺序不能颠倒。
<!ELEMENT XXX (AAA* , BBB)>
			在xxx这个元素下可以有AAA元素0次或者多次
<!ATTLIST 元素名称   属性的名称 属性的类型 属性的选项  
		          属性的名称 属性的类型 属性的选项 
		          ······		>
#REQUIRED：
			必填的
#IMPLIED：
			选填的
NMTOKEN：
			字母，数字，. - _ :  不能以：开头
NMTOKENS: NMTOKEN+空格
ID：
			表示这个属性的值在整个xml文件中不允许有重复的。且ID的值必须以字母开头
IDREF：
			表示这儿属性的值需要参照其他节点中有属性被ID所修饰的值。
IDREFS：
			如果这个属性别这个关键字所修饰，表示这个属性的值可以填写多个，但是每个必须要来源于被ID所修饰的属性的值。
true ( yes | no )：
			表示true这个属性的值 只是能是yes or no
true ( yes | no ) "yes"： 
		""内的内容表示将会作为这个属性的默认值。
EMPTY：
		修饰元素，如果元素被EMPTY所修饰，表示这个元素中不能含有内容。
```

### Xpath

```tex
/  :  	定位元素，表示到一个元素的绝对路径
//   :	则表示选择文档中所有满足双斜线//之后规则的元素(无论层级关系)
*   ： 	全部，表示选择所有由星号之前的路径所定位的元素
//x[last()] :	表示获取这一组元素的最后元素
//@id：	选择所有的id属性
// xx[@id] :	选择有id属性的xx元素
//xx[@id='a']: 	选择所有的含有id属性 切id为a的xx元素
//xx[normalize-space(@id)='a']：	选择所有的含有id属性 并且id去掉首位空格后为a的xx元素
//*[count(xx)=3]：	选择含有3个xx元素的元素
//*[count(*)=3]：	选择含有3个子元素的元素
//*[name()='BBB']：	name函数表示获取元素名称 等于//BBB
//*[starts-with(name(),'B')]：	获取以什么开头的元素
//*[contains(name(),'C')]：	获取元素名称中包含该字符的元素
//*[string-length(name()) = 3] ：	通过元素名称的长度来获取元素  =  < >都行。应该用&lt代替<，&gt代替>  因为会影响到标签
//CCC | //BBB：		| 表示合并

轴： （所有的*可换成指定元素）
child::          表示去所有的子元素 ，没啥用，和不写一样
descendant::*	取所有的后代元素，不包括本节点
//DDD/parent::*	选择DDD元素的所有父节点
//FFF/ancestor::*	获取FF的祖先元素
following-sibling::*	取后兄弟元素
preceding-sibling::*	取前兄弟元素
descendant-or-self::*	获取所有的后代元素并且包含自己
ancestor-or-self::*	获取所有的祖先元素并且包含自己
self::*			表示自己
//BBB[position() mod 2 = 0 ]	选择偶数位置的BBB元素，从1开始
//BBB[ position() = floor(last() div 2 + 0.5) or position() = ceiling(last() div 2 + 0.5) ]
选择中间的BBB元素，可能是一个，也可能是两个。
```

### Xml解析

#### DOM

```java
try {
		// 1.创建XML文档解析器
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//2..创建一个File对象指向你需要解析的xml文件
			File xmlFile = new File("catalog.xml");//添加绝对路径
		//3.将文件交给解析器进行解析
			// .创建 DocumentBuilder(DOM方式在JDK中的解析器对象)
			DocumentBuilder builder = factory.newDocumentBuilder();
			// 解析XML文檔，這樣XML文檔就會加載到內存中，形成樹狀結構
			Document document = builder.parse(xmlFile);
		//4.处理解析结果
			// 获取根节点
			Element rootElement = document.getDocumentElement();
			System.out.println("Root Element is: " + rootElement.getTagName());
			//调用自定义的方法
			visitNode(null, rootElement);

		} catch (SAXException e) {
			System.out.println(e.getMessage());

		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
		/*Element previousNode,  父节点 Element visitNode 本节点*/
	public static void visitNode(Element previousNode, Element visitNode) {
		if (previousNode != null) {
			System.out.println("Element " + previousNode.getTagName()+ " has element:");
		}
		System.out.println("Element Name: " + visitNode.getTagName());
		//hasAttributes返回此节点（如果它是一个元素）是否具有任何属性
		if (visitNode.hasAttributes()) {
				//getTagName() 元素的名称
			System.out.println("Element " + visitNode.getTagName()+ " has attributes: ");
			//NamedNodeMap getAttributes();包含此节点的属性的 NamedNodeMap（如果它是 Element）；否则为 null。 
			NamedNodeMap attributes = visitNode.getAttributes();
			for (int j = 0; j < attributes.getLength(); j++) {
				//Attr 接口表示 Element 对象中的属性
				Attr attribute = (Attr) (attributes.item(j));
					//getName 属性的名字 getValue属性的值
				System.out.println("Attribute:" + attribute.getName()+ " with value " + attribute.getValue());
			}
		}
		//getChildNodes包含此节点的所有子节点的 NodeList
		NodeList nodeList = visitNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			//Node表示该文档树中的单个节点
			Node node = nodeList.item(i);
			//判断 节点的类型为元素Element还是 Text 节点
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				//递归调用
				visitNode(visitNode, element);
			} else if (node.getNodeType() == Node.TEXT_NODE) {
				String str = node.getNodeValue().trim();
				if (str.length() > 0) {
					System.out.println("Element Text: " + str);
				}
			}
		}
	}

```

#### SAX

```java
public void parseDocument() {
		try {
			// 创建SAX解析器工厂对象
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			//DefaultHandler  SAX2 事件处理程序的默认基类 
			DefaultHandler handler = new CustomSAXHandler();
  			//parse()使用指定的 DefaultHandler 将指定xml解析 。
			saxParser.parse(new File("catalog.xml"), handler);
			CustomSAXHandler h = (CustomSAXHandler) handler;
			//得到list集合，处理结果集
			List list = h.getList();
			for (int i = 0; i < list.size(); i++) {
				Journal j = (Journal) list.get(i);
				Article a = j.getArticle();
				System.out.print("<<<<<<" + a.getAuthor() + "\t");
				System.out.println("<<<<<<" + a.getTitle());
			}
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (IOException e) {
		}
	}

	private class CustomSAXHandler extends DefaultHandler {
		private List list;
		private Journal j;
		private Article a;
		private boolean flag;
		public List getList() {
			return this.list;
		}
		public CustomSAXHandler() {
		}
		public void startDocument() throws SAXException {
			System.out.println("Event Type: Start Document");
		}
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			/*
			 * System.out.println("Event Type: Start Element");
			 * System.out.println("Element Name:" + qName); for (int i = 0; i <
			 * attributes.getLength(); i++) { System.out.println("Attribute
			 * Name:" + attributes.getQName(i)); System.out.println("Attribute
			 * Value:" + attributes.getValue(i)); }
			 */
			if ("catalog".equals(qName)) {
				list = new ArrayList();
				for (int i = 0; i < attributes.getLength(); i++) {
					System.out.println("Name:" + attributes.getQName(i) + "\t"
							+ "value:" + attributes.getValue(i));
				}
			}
			if ("journal".equals(qName)) {
				j = new Journal();
			}
			if ("article".equals(qName)) {
				a = new Article();
			}
			if ("title".equals(qName)) {
				flag = true;
			}

		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// System.out.println("Event Type: End Element");
			if ("title".equals(qName))
				flag = false;
			if ("journal".equals(qName)) {
				j.setArticle(a);
				list.add(j);
			}
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// System.out.println(ch);
			// System.out.println("Event Type: Text");
			String str = new String(ch, start, length).trim();
			// System.out.println(str);
			if (a != null && str != null && str.length() > 0) {
				if (flag)
					a.setTitle(str);
				else
					a.setAuthor(str);
			}
		}

		public void error(SAXParseException e) throws SAXException {
			System.out.println("Error: " + e.getMessage());
		}

		public void fatalError(SAXParseException e) throws SAXException {
			System.out.println("Fatal Error: " + e.getMessage());
		}

		public void warning(SAXParseException e) throws SAXException {
			System.out.println("Warning: " + e.getMessage());
		}
	}

}

```

#### STAX

```java
public class StAXParser {
	public void parseXMLDocument() {
		try {
			// STAX解析器工厂对象
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			//获取要解析的xml的流对象
			InputStream input = new FileInputStream(new File("catalog.xml"));
			//用一个io的inputstream创建一个XMLStreamReader
			XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(input);
			//循环判断是否有更多的解析式件
			while (xmlStreamReader.hasNext()) {

				int event = xmlStreamReader.next();
				//开始文档
				if (event == XMLStreamConstants.START_DOCUMENT) {
					System.out.println("Event Type:START_DOCUMENT");
				}
				//开始元素
				if (event == XMLStreamConstants.START_ELEMENT) {
					System.out.println("Event Type: START_ELEMENT");
							//getLocalName返回当前元素的名称
					System.out.println("Element Local Name:"+ xmlStreamReader.getLocalName());
							//getAttributeCount()返回属性的个数 遍历得到所有的属性
					for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
						System.out.println("Attribute Local Name:"+ xmlStreamReader.getAttributeLocalName(i));
						System.out.println("Attribute Value:"+ xmlStreamReader.getAttributeValue(i));
					}
				}
				//文本
				if (event == XMLStreamConstants.CHARACTERS) {
					System.out.println("Event Type: CHARACTERS");
					//去掉左右空格后的文本
					String str = xmlStreamReader.getText().trim();
					//如果不为空，处理得到的文本
					if (str != null && str.length() > 0)
						System.out.println("Text:" + xmlStreamReader.getText());
				}
				//文档结束标签
				if (event == XMLStreamConstants.END_DOCUMENT) {
					System.out.println("Event Type:END_DOCUMENT");
				}
				//元素结束标签
				if (event == XMLStreamConstants.END_ELEMENT) {
					System.out.println("Event Type: END_ELEMENT");
				}

			}
		} catch (FactoryConfigurationError e) {
			System.out.println("FactoryConfigurationError" + e.getMessage());
		} catch (XMLStreamException e) {
			System.out.println("XMLStreamException" + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException" + e.getMessage());
		}

	}
}

```



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

