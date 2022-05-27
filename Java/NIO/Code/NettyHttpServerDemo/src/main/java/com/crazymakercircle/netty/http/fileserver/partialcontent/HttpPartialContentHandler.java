package com.crazymakercircle.netty.http.fileserver.partialcontent;

import com.crazymakercircle.http.HttpProtocolHelper;
import com.crazymakercircle.http.TransShard;
import com.crazymakercircle.util.FileUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
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

@Slf4j
public class HttpPartialContentHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{


    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception
    {
        //通过channel 缓存 Http 的协议版本，以及是否为长连接
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

        /**
         * （1）如果请求的是目录，则发送目录
         * （2）如果请求的文件不存在或者不可以读，则发送错误信息
         * （3）如果请求的文件是有效的文件，则返回
         */
        File file = sendErrorOrDirectory(ctx, request);
        if (file == null) return;
        //文件长度
        long fileLength = FileUtil.getFileSize(file);
        //文件名称
        String fileName = file.getName();
        //传输分片
        TransShard shard = new TransShard(fileLength);
        //计算传输分片，并且获取响应
        HttpResponse response = shard.compute(ctx, request);
        if (null == response)
        {
            return;
        }
        //设置响应头
        HttpProtocolHelper.setContentTypeHeader(response, file);
        HttpProtocolHelper.setDateAndCacheHeaders(response, file);
        HttpProtocolHelper.setKeepAlive(ctx, response);

        //发送响应行和响应头
        ctx.write(response);
        //随机访问文件实例
        RandomAccessFile raf = HttpProtocolHelper.openFile(ctx, file);

        HttpChunkedInput httpChunkedInput = new HttpChunkedInput(
                new ChunkedFile(raf, shard.getStart(), shard.getLength(), shard.HTTPS_CHUNK_SIZE));

        /**
         * 传输传完回调
         */
        final CompletableFuture<Boolean> transferFuture = new CompletableFuture<>();

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
                    log.info("文件：{}，进度：{}", fileName, progress);
                } else
                {
                    log.info("文件：{}，进度：{}", fileName, progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future)
            {

                if (!future.isSuccess())
                {
                    // 处理失败
                    Throwable cause = future.cause();
                    cause.printStackTrace();
                    log.info("分片传输，文件传输失败：{}", fileName);
                    transferFuture.completeExceptionally(cause);
                } else
                {
                    transferFuture.complete(true);
                }

            }
        });
        transferFuture.whenComplete((finished, cause) ->
        {
            if (finished)
            {
                log.info("分片传输，文件传输完成：{}", fileName);
                ChannelFuture lastContentFuture =
                        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

                //不是长连接，完成后关闭通道
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            } else
            {
                log.info("分片传输异常，关闭连接通道：{}", ctx.channel());
                ctx.channel().close();
            }
            closeQuietly(raf);

        });
        // 传输分片的内容
        ctx.writeAndFlush(httpChunkedInput, progressivePromise);

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
