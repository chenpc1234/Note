package com.crazymakercircle.secure.netty.securechat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientChatHandler extends SimpleChannelInboundHandler<String>
{
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 输出从服务端收到的响应
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception
    {
        System.out.println(msg);
    }
}
