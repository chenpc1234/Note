package com.crazymakercircle.mbeanDemo;
 
import java.lang.management.ManagementFactory;
 
import javax.management.MBeanServer;
import javax.management.ObjectName;
 
/**
 * JMX App Demo
 */
public class MbeanDemoApp {



	public static void main(String[] args) throws Exception {
		// 创建MBeanServer
		MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
		// 新建MBean ObjectName, 在MBeanServer里标识注册的MBean
		ObjectName objectName =
				new ObjectName("com.crazymakercircle.mbeanDemo.impl:type=Echo");
		// 创建MBean
		EchoMBean mbean = new Echo();
		// 在MBeanServer里注册MBean, 标识为ObjectName(com.tenpay.jmx:type=Echo)
		beanServer.registerMBean(mbean, objectName);
		// 在MBeanServer里调用已注册的EchoMBean的echoback方法
		beanServer.invoke(objectName,
				"echoBack", new Object[] { "foo"}, new String[] {"java.lang.String"});

		Thread.sleep(Long.MAX_VALUE);
	}
 
}