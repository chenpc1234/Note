package com.crazymakercircle.netty.websocket;

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
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class WebSocketEchoServer
{
    //流水线装配器
    static class EchoInitializer extends ChannelInitializer<SocketChannel>
    {


        @Override
        public void initChannel(SocketChannel ch)
        {
            ChannelPipeline pipeline = ch.pipeline();
            //请求解码器和响应编码器,，等价于下面两行
            //pipeline.addLast(new HttpServerCodec());
            //请求解码器
            pipeline.addLast(new HttpRequestDecoder());
            //响应编码器
            pipeline.addLast(new HttpResponseEncoder());
            // HttpObjectAggregator 将HTTP消息的多个部分合成一条完整的HTTP消息
            // HttpObjectAggregator 用于解析Http完整请求
            // 把多个消息转换为一个单一的完全FullHttpRequest或是FullHttpResponse，
            // 原因是HTTP解码器会在解析每个HTTP消息中生成多个消息对象如 HttpRequest/HttpResponse,HttpContent,LastHttpContent
            pipeline.addLast(new HttpObjectAggregator(65535));
            // WebSocket报文处理，包括数据压缩与解压
            // pipeline.addLast(new WebSocketServerCompressionHandler());
            // WebSocketServerProtocolHandler是配置websocket的监听地址/协议包长度限制
            pipeline.addLast(new WebSocketServerProtocolHandler("/ws", "echo", true, 10 * 1024));
            //增加网页的处理逻辑
            pipeline.addLast(new WebPageHandler());
            //TextWebSocketFrameHandler 是自定义逻辑处理器，
            pipeline.addLast(new TextWebSocketFrameHandler());
        }
    }


    /**
     * 启动
     *
     * @throws Exception
     */
    public static void start(String ip) throws Exception
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
                    .childHandler(new EchoInitializer());

            // 监听端口，返回同步通道
            Channel ch = b.bind(SystemConfig.SOCKET_SERVER_PORT).sync().channel();

            log.info("WebSocket服务已经启动 http://{}:{}/",
                    ip, SystemConfig.SOCKET_SERVER_PORT);

            ch.closeFuture().sync();
        } finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
