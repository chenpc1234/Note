package com.crazymakercircle.secure.test.SecureChat;

import com.crazymakercircle.config.SystemConfig;
import com.crazymakercircle.secure.netty.securechat.SecureChatClient;
import com.crazymakercircle.secure.netty.securechat.SecureChatServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
/**
 * 基于Netty的简单安全聊天器，测试用例
 * create by 尼恩 @ 疯狂创客圈
 **/
public class SecureChatTester
{
    /**
     * 启动安全聊天器服务端
     */
    @Test
    public void startSecureChatServer() throws Exception
    {
        new SecureChatServer().start(SystemConfig.SOCKET_SERVER_PORT);
    }

    /**
     * 启动安全聊天器客户端
     */
    @Test
    public void startClient() throws Exception
    {
        new SecureChatClient().start("localhost", SystemConfig.SOCKET_SERVER_PORT);
    }
}
