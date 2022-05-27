package com.crazymakercircle.secure.netty.securechat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.crazymakercircle.ssl.SSLContextHelper.createClientSSLContext;
/**
 * 基于Netty的简单安全聊天器客户端程序
 */
public class SecureChatClient
{


    static class SecureChatClientInitializer extends ChannelInitializer<SocketChannel>
    {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception
        {
            ChannelPipeline pipeline = ch.pipeline();
            //创建客户端SSL 上下文
            SSLContext clientSSLContext = createClientSSLContext();
            //创建客户端 SSL 引擎
            SSLEngine sslEngine = clientSSLContext.createSSLEngine();
            //在握手的时候，使用客户端模式
            sslEngine.setUseClientMode(true);
            //设置需要验证对端(服务端)身份，需服务端证实自己的身份
            sslEngine.setNeedClientAuth(true);
            //创建ssl处理器，并加入到流水线
            pipeline.addLast("ssl", new SslHandler(sslEngine));

            // 添加分包处理器
            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
            //添加字符串解码器
            pipeline.addLast("decoder", new StringDecoder());
            //添加字符串编码器
            pipeline.addLast("encoder", new StringEncoder());
            //添加
            pipeline.addLast("handler", new ClientChatHandler());
        }

    }

    /**
     * 开始客户端
     */
    public void start(String host, int port) throws Exception
    {
        EventLoopGroup group = new NioEventLoopGroup();
        try
        {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .handler(new SecureChatClientInitializer());
            // 开始连接服务器
            Channel ch = b.connect(host, port).sync().channel();
            // 从控制台获取输入的内容
            ChannelFuture writeFuture = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            for (; ; )
            {
                String line = reader.readLine();
                if (line != null)
                {
                    //发送控制台输入内容
                    writeFuture = ch.writeAndFlush(line + "\r\n");
                }
                // 如果输入bye,表示终止连接
                if ("bye".equals(line.toLowerCase()))
                {
                    ch.closeFuture().sync();
                    break;
                }
            }
            // 发送完成之后，再接收下一轮的输入
            if (writeFuture != null)
            {
                writeFuture.sync();
            }
        } finally
        {
            //优雅关闭
            group.shutdownGracefully();
        }
    }

}
