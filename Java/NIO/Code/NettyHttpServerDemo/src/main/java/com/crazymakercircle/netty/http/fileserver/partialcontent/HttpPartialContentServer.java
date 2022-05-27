package com.crazymakercircle.netty.http.fileserver.partialcontent;

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
public final class HttpPartialContentServer
{
    static class HttpZeroCopyInitializer extends ChannelInitializer<SocketChannel>
    {


        @Override
        public void initChannel(SocketChannel ch)
        {
            ChannelPipeline pipeline = ch.pipeline();

            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(65536));
            pipeline.addLast(new ChunkedWriteHandler());
            pipeline.addLast(new HttpPartialContentHandler());
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

            log.info("断点续传分段下载，文件服务已经启动 http://{}:{}/"
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
