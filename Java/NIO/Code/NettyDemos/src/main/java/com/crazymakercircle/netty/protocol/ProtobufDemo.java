package com.crazymakercircle.netty.protocol;

import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.util.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
public class ProtobufDemo
{


    public static MsgProtos.Msg buildMsg()
    {
        MsgProtos.Msg.Builder personBuilder = MsgProtos.Msg.newBuilder();
        personBuilder.setId(1000);
        personBuilder.setContent("疯狂创客圈:高性能学习社群");
        MsgProtos.Msg message = personBuilder.build();
        return message;
    }

    //第1种方式:序列化 serialization & 反序列化 Deserialization
    @Test
    public void serAndDesr1() throws IOException
    {
        MsgProtos.Msg message = buildMsg();
        //将Protobuf对象，序列化成二进制字节数组
        byte[] data = message.toByteArray();
        //可以用于网络传输,保存到内存或外存
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(data);
        data = outputStream.toByteArray();
        //二进制字节数组,反序列化成Protobuf 对象
        MsgProtos.Msg inMsg = MsgProtos.Msg.parseFrom(data);
        Logger.info("id:=" + inMsg.getId());
        Logger.info("content:=" + inMsg.getContent());
    }

    //第2种方式:序列化 serialization & 反序列化 Deserialization
    @Test
    public void serAndDesr2() throws IOException
    {
        MsgProtos.Msg message = buildMsg();
        //序列化到二进制流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeTo(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        //从二进流,反序列化成Protobuf 对象
        MsgProtos.Msg inMsg = MsgProtos.Msg.parseFrom(inputStream);
        Logger.info("id:=" + inMsg.getId());
        Logger.info("content:=" + inMsg.getContent());
    }


    //第3种方式:序列化 serialization & 反序列化 Deserialization
    //带字节长度：[字节长度][字节数据],解决粘包问题
    @Test
    public void serAndDesr3() throws IOException
    {
        MsgProtos.Msg message = buildMsg();
        //序列化到二进制流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeDelimitedTo(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        //从二进流,反序列化成Protobuf 对象
        MsgProtos.Msg inMsg = MsgProtos.Msg.parseDelimitedFrom(inputStream);
        Logger.info("id:=" + inMsg.getId());
        Logger.info("content:=" + inMsg.getContent());


    }

    static class MessageHandler extends ChannelInboundHandlerAdapter
    {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
        {
            ProtoMsg.MessageNotification notification = (ProtoMsg.MessageNotification) msg;

            System.out.println("accept client notification:[" + notification.toString() + "]");

        }

    }
    private ProtoMsg.MessageNotification buildProtoBufMsg(String content){
        ProtoMsg.MessageNotification.Builder builder =   ProtoMsg.MessageNotification.newBuilder();
        builder.setMsgType(1);
        builder.setJson(content);
        return builder.build();
    }

    /**
     * 字符串解码器的使用实例
     */
    @Test
    public void testProtobufDecoder()
    {
        ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>()
        {
            protected void initChannel(EmbeddedChannel ch)
            {
                // protobufDecoder仅仅负责编码，并不支持读半包，所以在之前，一定要有读半包的处理器。
                // 有三种方式可以选择：
                // 使用netty提供ProtobufVarint32FrameDecoder
                // 继承netty提供的通用半包处理器 LengthFieldBasedFrameDecoder
                // 继承ByteToMessageDecoder类，自己处理半包

                // 半包的处理
                ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                // 需要解码的目标类
                ch.pipeline().addLast(new ProtobufDecoder(ProtoMsg.MessageNotification.getDefaultInstance()));

                ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                ch.pipeline().addLast(new ProtobufEncoder());

                ch.pipeline().addLast(new MessageHandler());
            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(i);
        for (int j = 0; j < 100; j++)
        {

            ProtoMsg.MessageNotification pkg = buildProtoBufMsg("json "+j);

            channel.writeInbound(pkg);
            channel.flush();
        }
        try
        {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


}
