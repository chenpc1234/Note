package com.crazymakercircle.netty.handler;

import com.crazymakercircle.util.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;

import java.io.RandomAccessFile;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
public class FileRegionDemo extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RandomAccessFile raf = null;
        long length = -1;
        try {
            // 1. 通过 RandomAccessFile 打开一个文件.
            raf = new RandomAccessFile(msg.toString(), "r");
            length = raf.length();
            // if SSL enabled - cannot use zero-copy file transfer.
            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, length));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            if (length < 0 && raf != null) {
                raf.close();
            }
        }
        super.channelRead(ctx, msg);
    }

}