package com.crazymakercircle.netty.http.fileserver.trunkedstream;

import com.crazymakercircle.http.HttpProtocolHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedInput;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.CompletableFuture;

import static com.crazymakercircle.http.HttpProtocolHelper.sendErrorOrDirectory;
import static com.crazymakercircle.util.IOUtil.closeQuietly;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
public class HttpChunkedStreamServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{

    private final CompletableFuture<Boolean> transferFuture = new CompletableFuture<>();

    private FullHttpRequest request;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception
    {
        this.request = request;

        HttpProtocolHelper.cacheHttpProtocol(ctx, request);
        //如果是不知道长度的输出流，则不能设置Wie长连接
        HttpProtocolHelper.setKeepAlive(ctx, false);


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
        File file = sendErrorOrDirectory(ctx, request);
        if (file == null) return;
        /**
         * 文件名称
         */
        String fname = file.getName();
        //随机访问文件实例
        RandomAccessFile raf = HttpProtocolHelper.openFile(ctx, file);

        //文件长度
        long fileLength = raf.length();

        //设置响应行和响应头
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpProtocolHelper.setContentTypeHeader(response, file);
        HttpUtil.setContentLength(response, fileLength);
        HttpProtocolHelper.setDateAndCacheHeaders(response, file);
        HttpProtocolHelper.setKeepAlive(ctx, response);
        response.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);


        //发送响应行和响应头
        ctx.write(response);

        //管道消息传输承诺
        ChannelProgressivePromise progressivePromise = ctx.newProgressivePromise();

        //并注册一个ChannelProgressiveFutureListener 进度监听器
        progressivePromise.addListener(new ChannelProgressiveFutureListener()
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
                    transferFuture.completeExceptionally(cause);
                } else
                {
                    transferFuture.complete(true);
                }
                closeQuietly(raf);


            }
        });
        transferFuture.whenComplete((finished, cause) ->
        {
            if (finished)
            {
                log.info("文件传输完成：{}", fname);
                ChannelFuture lastContentFuture =
                        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

                //不是长连接，关闭
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            } else
            {
                log.info("关闭连接通道：{}", ctx.channel());
                ctx.channel().close();

            }

        });
        // 发送内容
        ChunkedInput<ByteBuf> chunkedFile = new ChunkedFile(raf, 0, fileLength, 8192);
        ctx.writeAndFlush(
                new HttpChunkedInput(chunkedFile), progressivePromise);

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
