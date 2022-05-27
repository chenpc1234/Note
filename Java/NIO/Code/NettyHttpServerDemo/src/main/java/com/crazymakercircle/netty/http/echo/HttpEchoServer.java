package com.crazymakercircle.netty.http.echo;

import com.crazymakercircle.config.SystemConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HttpEchoServer
{
    //
    static class HttpZeroCopyInitializer extends ChannelInitializer<SocketChannel>
    {


        @Override
        public void initChannel(SocketChannel ch)
        {
            ChannelPipeline pipeline = ch.pipeline();
            //请求解码器和响应编码器,等价于下面两行
            // pipeline.addLast(new HttpServerCodec());
            //请求解码器
            pipeline.addLast(new HttpRequestDecoder());
            //响应编码器
            pipeline.addLast(new HttpResponseEncoder());
            // HttpObjectAggregator 将HTTP消息的多个部分合成一条完整的HTTP消息
            // HttpObjectAggregator 用于解析Http完整请求
            // 把多个消息转换为一个单一的完全FullHttpRequest或是FullHttpResponse，
            // 原因是HTTP解码器会在解析每个HTTP消息中生成多个消息对象如 HttpRequest/HttpResponse,HttpContent,LastHttpContent
            pipeline.addLast(new HttpObjectAggregator(65535));
            // 自定义的业务handler
            pipeline.addLast(new HttpEchoHandler());
        }
    }

    /**
     * 启动服务，入口方法一
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        start();
    }
    /**
     * 启动服务
     */
    public static void start() throws InterruptedException
    {
        // 创建连接监听reactor 线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 创建连接处理 reactor 线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try
        {
            //服务端启动引导实例
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new HttpZeroCopyInitializer());

            // 监听端口，返回监听通道
            Channel ch = b.bind(SystemConfig.SOCKET_SERVER_PORT).sync().channel();

            log.info("HTTP ECHO 服务已经启动 http://{}:{}/"
                    , SystemConfig.SOCKET_SERVER_NAME
                    , SystemConfig.SOCKET_SERVER_PORT);
            // 等待服务端监听到端口关闭
            ch.closeFuture().sync();
        } finally
        {
            // 优雅地关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
