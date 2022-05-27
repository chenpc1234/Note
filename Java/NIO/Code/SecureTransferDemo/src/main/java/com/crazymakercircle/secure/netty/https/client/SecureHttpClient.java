package com.crazymakercircle.secure.netty.https.client;

import com.crazymakercircle.config.SystemConfig;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.crazymakercircle.ssl.SSLContextHelper.createClientSSLContext;

/**
 * HTTPS回显服务器的客户端程序
 * 通过JDK自带的HttpURLConnection发送HTTPS请求
 */
@Slf4j
public class SecureHttpClient
{
    /**
     * 通过JDK自带的HttpURLConnection发送HTTPS请求
     * @param path 请求地址
     */
    public static void sentRequest(String path) throws Exception
    {
        //创建客户端SSLContext上下文
        SSLContext clientSSLContext = createClientSSLContext();
        //创建安全套接字工厂
        SSLSocketFactory factory = clientSSLContext.getSocketFactory();

        //主机名称校验
        HostnameVerifier hostnameVerifier  = new HostnameVerifier()
        {
            public boolean verify(String hostname, SSLSession sslsession)
            {
                //验证请求的主机名称，这里假设只能请求服务端的配置的主机名
                if (SystemConfig.SOCKET_SERVER_IP.equals(hostname))
                {
                    return true;
                } else
                {
                    log.error("主机名称校验失败:",SystemConfig.SOCKET_SERVER_IP);
                    return false;
                }
            }
        };
        //设置连接的主机名称校验
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier );
        //设置连接的安全套接字工厂
        HttpsURLConnection.setDefaultSSLSocketFactory(factory);

        URL url = new URL(path);

        //打开连接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //获取响应码
        int code = conn.getResponseCode();
        // log.info("收到消息", conn.getResponseMessage());
        if (code < 400)
        {
            //输入流
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            StringBuffer buffer = new StringBuffer();
            //累积完成的长度
            long finished = 0;
            int len = 0;
            byte[] buff = new byte[1024 * 8];
            while ((len = bis.read(buff)) != -1)
            {
                buffer.append(new String(buff, "UTF-8"));
                finished += len;
                log.info("共完成传输字节数 {}", finished);
            }
            System.out.println("echo = " + buffer.toString());
        }
    }
}
