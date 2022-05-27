package com.crazymakercircle.secure.test.oio;

import com.crazymakercircle.secure.oio.SSLEchoClient;
import com.crazymakercircle.secure.oio.SSLEchoServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
@Slf4j

public class OIOTester
{
    /**
     * 服务端测试用例
     * @throws Exception
     */
    @Test
    public void startOIOServer() throws Exception
    {
        SSLEchoServer.start();

    }

    /**
     * 客户端测试用例
     * @throws Exception
     */
    @Test
    public void startSSLEchoClient() throws Exception
    {
        // 抓包说明：由于WireShark只能抓取经过网卡的包，
        // 如果要抓取本地的调试包， 需要通过指令route指令增加服务器IP的路由配置
        // 让发往服务器的报文，首先发送到被抓包工具监控的网卡所指向的网关
        // route add 增加路由，表示发往192.168.0.5（网卡IP）的请求下一跳网关为192.168.0.1
        // route add 192.168.0.5 mask 255.255.255.255 192.168.0.1
        SSLEchoClient.connect("192.168.0.5");

    }
}
