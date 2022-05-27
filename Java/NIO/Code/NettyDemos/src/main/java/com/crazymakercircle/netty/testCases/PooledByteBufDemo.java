package com.crazymakercircle.netty.testCases;

import com.crazymakercircle.util.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import org.junit.Test;


public class PooledByteBufDemo {

    @Test
    public void poolTest() {
        Logger.info("测试buf回收====");
        ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
        // tiny
        ByteBuf buf1 = allocator.directBuffer(495); // 分配的内存最大长度为496
        Logger.info("buf1: 0x%X%n", buf1.memoryAddress());
        buf1.release(); // 此时会被回收到tiny 的512b格子中
    }

    @Test
    public void testUnpooledDirectByteBuf() {

        PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;

        ByteBuf buffer = allocator.buffer(1024);

        print("allocate ByteBuf(9, 100)", buffer);


    }


    private static void print(String action, ByteBuf buffer) {
        System.out.println("after ===========" + action + "============");
        System.out.println("capacity(): " + buffer.capacity());
        System.out.println("maxCapacity(): " + buffer.maxCapacity());
        System.out.println("readerIndex(): " + buffer.readerIndex());
        System.out.println("readableBytes(): " + buffer.readableBytes());
        System.out.println("isReadable(): " + buffer.isReadable());
        System.out.println("writerIndex(): " + buffer.writerIndex());
        System.out.println("writableBytes(): " + buffer.writableBytes());
        System.out.println("isWritable(): " + buffer.isWritable());
        System.out.println("maxWritableBytes(): " + buffer.maxWritableBytes());
        System.out.println();
    }
}