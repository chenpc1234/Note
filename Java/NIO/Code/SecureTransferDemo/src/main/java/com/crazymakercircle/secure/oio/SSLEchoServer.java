package com.crazymakercircle.secure.oio;

import com.crazymakercircle.config.SystemConfig;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.crazymakercircle.config.SystemConfig.SOCKET_SERVER_PORT;
import static com.crazymakercircle.ssl.SSLContextHelper.createServerSSLContext;
import static com.crazymakercircle.util.IOUtil.closeQuietly;

//服务端
@Slf4j
public class SSLEchoServer
{
    //服务端SSL监听套接字
    static SSLServerSocket serverSocket;

    public static void start()
    {
        try
        {
            //创建服务端SSL上下文实例
            SSLContext serverSSLContext = createServerSSLContext();
            SSLServerSocketFactory sslServerSocketFactory = serverSSLContext.getServerSocketFactory();
            //通过服务端SSL上下文实例，创建服务端SSL监听套接字
            serverSocket = (SSLServerSocket)
                    sslServerSocketFactory.createServerSocket(SOCKET_SERVER_PORT);
            //单向认证：在服务端设置不需要验证对端身份，无需客户端证实自己的身份
            serverSocket.setNeedClientAuth(true);
            //在握手的时候，使用服务端模式
            serverSocket.setUseClientMode(false);

            String[] supported = serverSocket.getEnabledCipherSuites();
            serverSocket.setEnabledCipherSuites(supported);
            log.info("SSL OIO ECHO 服务已经启动 {}:{}",
                    SystemConfig.SOCKET_SERVER_NAME, SystemConfig.SOCKET_SERVER_PORT);
            // 监听和接收客户端连接
            while (!Thread.interrupted())
            {
                Socket client = serverSocket.accept();
                System.out.println(client.getRemoteSocketAddress());
                // 向客户端发送接收到的字节序列
                OutputStream output = client.getOutputStream();
                // 当一个普通 socket 连接上来, 这里会抛出异常
                InputStream input = client.getInputStream();
                byte[] buf = new byte[1024];
                int len = 0;
                StringBuffer buffer = new StringBuffer();
                while ((len = input.read(buf)) != -1)
                {
                    String sf = new String(buf, 0, len, "UTF-8");
                    log.info("服务端收到：{}", sf);
                    buffer.append(sf);
                    if (sf.contains("\r\n\r\n"))
                    {
                        break;
                    }
                }
                //发送消息到客户端
                output.write(buffer.toString().getBytes("UTF-8"));
                output.flush();
                // 关闭socket连接
                closeQuietly(input);
                closeQuietly(output);
                closeQuietly(client);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {  // 关闭serverSocket监听套接字
            closeQuietly(serverSocket);
        }
    }

}
