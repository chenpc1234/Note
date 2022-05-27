package com.crazymakercircle.netty.http.fileserver.zerocopy;

import com.crazymakercircle.http.HttpProtocolHelper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;

import static com.crazymakercircle.util.IOUtil.closeQuietly;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
public class HttpStaticFileZeroCopyHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{


    private FullHttpRequest request;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception
    {
        this.request = request;

        HttpProtocolHelper.cacheHttpProtocol(ctx, request);


        if (!request.decoderResult().isSuccess())
        {
            HttpProtocolHelper.sendError(ctx, BAD_REQUEST);
            return;
        }

        if (!GET.equals(request.method()))
        {
            HttpProtocolHelper.sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }

        // 发送目录或者错误信息，如果是文件，则返回
        File file = HttpProtocolHelper.sendErrorOrDirectory(ctx, request);
        if (file == null) return;
        /**
         * 文件名称
         */
        String fname = file.getName();

        //随机访问文件实例
        RandomAccessFile raf = HttpProtocolHelper.openFile(ctx, file);
        //文件长度
        long fileLength = raf.length();


        //设置响应头
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpUtil.setContentLength(response, fileLength);
        HttpProtocolHelper.setContentTypeHeader(response, file);
        HttpProtocolHelper.setDateAndCacheHeaders(response, file);
        HttpProtocolHelper.setKeepAlive(ctx, response);


        //发送响应行和响应头
        ctx.write(response);
        // 以该文件的完整长度创建一个新的 DefaultFileRegion
        DefaultFileRegion fileRegion = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
        // 发送内容
        // 发送该 DefaultFileRegion
        ChannelFuture sendFileFuture =
                ctx.writeAndFlush(fileRegion, ctx.newProgressivePromise());
        //并注册一个ChannelProgressiveFutureListener 进度监听器
        sendFileFuture.addListener(new ChannelProgressiveFutureListener()
        {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total)
            {
                if (total < 0)
                {
                    //长度未知
                    log.info("文件：{}，进度：{}", fname, progress);
                } else
                {
                    log.info("文件：{}，进度：{}", fname, progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future)
            {

                if (!future.isSuccess())
                {
                    Throwable cause = future.cause();// 处理失败
                    // Do something
                    cause.printStackTrace();
                    log.info("文件传输失败：{}", fname);
                } else
                {
                    log.info("文件传输完成：{}", fname);

                }
                closeQuietly(raf);
            }

        });
        // 判断是否为长连接
        if (!HttpProtocolHelper.isKeepAlive(ctx))
        {
            //不是长连接，则关闭
            sendFileFuture.addListener(ChannelFutureListener.CLOSE);
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        if (ctx.channel().isActive())
        {
            HttpProtocolHelper.sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

}
