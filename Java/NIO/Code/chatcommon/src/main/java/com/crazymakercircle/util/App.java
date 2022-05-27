package com.crazymakercircle.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.MemoryUsage;
import java.util.LinkedHashMap;
import java.util.Map;

public class App {
    private static final String JMX_HOSTNAME = "192.168.214.128:1090";
    private static final String JMX_USERNAME = "jmxadmin";
    private static final String JMX_PASSWORD = "jmxpwd";

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        // 1.建立连接(mbsc)
        String jmxURL = "service:jmx:rmi:///jndi/rmi://" + JMX_HOSTNAME + "/jmxrmi";
        JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("jmx.remote.credentials", new String[] { JMX_USERNAME, JMX_PASSWORD });
        JMXConnector connector = JMXConnectorFactory.connect(serviceURL, map);
        MBeanServerConnection mbsc = connector.getMBeanServerConnection();

        // 2.获取远程应用数据
        ObjectName objectName = new ObjectName("java.lang:type=Memory");
        Object heapMemoryUsage = mbsc.getAttribute(objectName, "HeapMemoryUsage");
        MemoryUsage usage = MemoryUsage.from((CompositeDataSupport) heapMemoryUsage);

        // 示例：如果 Heap 使用率大于 60%，则请求 FullGC
        long usedRate = usage.getUsed() * 100 / usage.getMax();
        logger.info("usedRate=" + usedRate +" ,Used "+ usage.getUsed()+" , Max"+ usage.getMax());
        if (usedRate > 30) {
            mbsc.invoke(objectName, "gc", null, null);
            logger.info("gc() OK.");
        }

        // 3.更多有用的 MBeans
        // (1)com.sun.management:type=HotSpotDiagnostic
        // dumpHeap(), getVMOption(), setVMOption()
        // (2)java.util.logging:type=Logging
        // getLoggerLevel(), setLoggerLevel()
    }
}