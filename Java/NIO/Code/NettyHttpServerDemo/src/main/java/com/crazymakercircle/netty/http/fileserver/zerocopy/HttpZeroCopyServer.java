package com.crazymakercircle.netty.http.fileserver.zerocopy;

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
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HttpZeroCopyServer
{
    static class HttpZeroCopyInitializer extends ChannelInitializer<SocketChannel>
    {


        @Override
        public void initChannel(SocketChannel ch)
        {
            ChannelPipeline pipeline = ch.pipeline();

            // 请求消息解码器和响应消息编码器
            pipeline.addLast(new HttpServerCodec());
            // 目的是将多个请求消息转换为一个整体请求消息
            pipeline.addLast(new HttpObjectAggregator(65536));
            // ChunkedWriteHandler 是用于大数据的分区传输
            // 主要用于处理大数据流，比如直接传输一个1G大小的文件肯定会撑暴内存;
            // 增加 ChunkedWriteHandler 之后就不用考虑这个问题了
            // ChunkedWriteHandler 将 ChunkedInput 类型的出站消息，分成小块(默认为8192)发送到下一站
            // 当出站消息读取完成,会发送一个标明长度为0的 LastHttpContent 报文，标示结束
            // 目的是支持异步大数据流传输
            pipeline.addLast(new ChunkedWriteHandler());

            // 业务逻辑
            pipeline.addLast(new HttpStaticFileZeroCopyHandler());
        }
    }


    public static void main(String[] args) throws Exception
    {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try
        {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new HttpZeroCopyInitializer());

            Channel ch = b.bind(SystemConfig.SOCKET_SERVER_PORT).sync().channel();

            log.info("文件服务已经启动 http://{}:{}/"
                    , SystemConfig.SOCKET_SERVER_NAME
                    , SystemConfig.SOCKET_SERVER_PORT);

            ch.closeFuture().sync();
        } finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
