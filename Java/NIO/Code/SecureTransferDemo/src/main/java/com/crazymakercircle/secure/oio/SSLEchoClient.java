package com.crazymakercircle.secure.oio;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.io.OutputStream;

import static com.crazymakercircle.config.SystemConfig.SOCKET_SERVER_PORT;
import static com.crazymakercircle.ssl.SSLContextHelper.createClientSSLContext;
import static com.crazymakercircle.util.IOUtil.closeQuietly;

//客户端
@Slf4j
public class SSLEchoClient
{
    //安全套接字
    static SSLSocket sslSocket;
    static OutputStream output;
    static InputStream input;

    public static void connect(String host)
    {
        try
        {

                //创建客户端SSL 上下文
                SSLContext clientSSLContext = createClientSSLContext();
                SSLSocketFactory factory = clientSSLContext.getSocketFactory();
                sslSocket = (SSLSocket) factory.createSocket(host, SOCKET_SERVER_PORT);
                //在握手的时候，使用客户端模式
                sslSocket.setUseClientMode(true);
                //设置需要验证对端身份，需验证服务端身份
                sslSocket.setNeedClientAuth(true);
                log.info("连接服务器成功");
                output = sslSocket.getOutputStream();
                input = sslSocket.getInputStream();
                output.write("hello\r\n\r\n".getBytes());
                output.flush();
                log.info("sent hello finished!");
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = input.read(buf)) != -1)
                {
                    log.info("客户端收到：{}", new String(buf, 0, len, "UTF-8"));
                }

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            closeQuietly(output);
            closeQuietly(input);
            closeQuietly(sslSocket);
        }
    }
}
