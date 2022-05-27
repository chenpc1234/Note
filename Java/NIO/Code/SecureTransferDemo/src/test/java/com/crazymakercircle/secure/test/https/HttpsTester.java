package com.crazymakercircle.secure.test.https;

import com.crazymakercircle.secure.netty.https.client.SecureHttpClient;
import com.crazymakercircle.secure.netty.https.server.NettyHttpsServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
/**
 * HTTPS回显服务器的 测试用例
 **/
@Slf4j
public class HttpsTester
{
    /**
     * HTTPS回显服务器的服务端程序 测试用例
     **/
    @Test
    public void startHttpsNettyServer() throws Exception
    {
        NettyHttpsServer.start();

    }
    /**
     * HTTPS回显服务器的客户端程序 测试用例
     **/
    @Test
    public void startClient() throws Exception
    {
        // 抓包说明：由于WireShark只能抓取经过所监控的网卡的数据包
        //故，请求到localhost的本地包，默认是不能抓取到的。
        // 如果要抓取本地的调试包， 需要通过指令route指令增加服务器IP的路由表项配置
        // 只有这样，让发往本地服务器的报文，经过网卡所指向的网关
        // 从而，发往localhost的本地包就能被抓包工具从监控网卡抓取到
        // 具备办法，通过增加路由表项完成，其命令为route add，下面是一个例子
        // route add 192.168.0.5 mask 255.255.255.255 192.168.0.1
        // 以上命令表示：目标为192.168.0.5报文，其发送的下一跳为192.168.0.1网关
        // 该路由项在使用完毕后，建议删除，其删除指令如下：
        // route delete 192.168.0.5 mask 255.255.255.255 192.168.0.1 删除
        // 如果没有删除，则所有本机报文都经过网卡绕一圈回来，会很耗性能
        // 不过该路由表项并没有保存，在电脑重启后失效
        // 提示：以上的本地IP和网关IP，需要结合自己的电脑网卡和网关去更改

        SecureHttpClient.sentRequest("https://192.168.0.5:18899/?param1=value1");
    }
}
