package com.crazymakercircle.mbeanDemo;

import com.crazymakercircle.mbeanDemo.EchoMBean;
import com.crazymakercircle.util.Logger;

/*
 * 该类名称必须与实现的接口的前缀保持一致（即MBean前面的名称
 *
 * 监控的类和MBean接口必需在同一包下
 */
public class Echo implements EchoMBean {

    @Override
    public void echoBack(String foo) {
        Logger.cfo("echo back: " + foo + "!");
    }

}