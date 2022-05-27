package com.crazymakercircle.secure.netty.https.server;

import com.crazymakercircle.http.HttpProtocolHelper;
import com.crazymakercircle.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

@Slf4j
public class HttpEchoHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{


    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception
    {
        if (!request.decoderResult().isSuccess())
        {
            HttpProtocolHelper.sendError(ctx, BAD_REQUEST);
            return;
        }
        /**
         * 缓存HTTP协议的版本号
         */
        HttpProtocolHelper.cacheHttpProtocol(ctx, request);

        Map<String, Object> echo = new HashMap<String, Object>();

        // 1.获取URI
        String uri = request.uri();
        echo.put("request uri", uri);


        // 2.获取请求方法
        HttpMethod method = request.method();
        echo.put("request method", method.toString());


        /**
         * 获取请求参数到 map
         */
        Map<String, Object> datas = paramsFromGet(request);
        echo.put("paramsFromGet", datas);

        /**
         * 转换成json字符串
         */
        String sendContent = JsonUtil.pojoToJson(echo);

        /**
         * 发送请求内容
         */
        HttpProtocolHelper.sendJsonContent(ctx, sendContent);
        return;


    }

    /*
     * 获取GET方式传递的参数
     */
    private Map<String, Object> paramsFromGet(FullHttpRequest fullHttpRequest)
    {

        Map<String, Object> params = new HashMap<String, Object>();

        if (fullHttpRequest.method() == HttpMethod.GET)
        {
            // 处理get请求，把URI分割成key-value形式
            QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.uri());
            Map<String, List<String>> paramList = decoder.parameters();
            //迭代处理请求参数列表
            for (Map.Entry<String, List<String>> entry : paramList.entrySet())
            {
                params.put(entry.getKey(), entry.getValue().get(0));
            }
            return params;
        } else
        {
            return null;
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        log.info("远程连接已经主动关闭， channel {}", ctx.channel());
//        cause.printStackTrace();
        if (ctx.channel().isActive())
        {
            HttpProtocolHelper.sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

}
