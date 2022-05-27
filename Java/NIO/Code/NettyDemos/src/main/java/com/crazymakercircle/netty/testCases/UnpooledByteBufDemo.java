package com.crazymakercircle.netty.testCases;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import org.junit.Test;


public class UnpooledByteBufDemo {


    @Test
    public void testUnpooledDirectByteBuf() {
        System.setProperty("java.vm.name", "Dalvik");

        UnpooledDirectByteBuf buffer = (UnpooledDirectByteBuf) UnpooledByteBufAllocator.DEFAULT.directBuffer(9, 100);

        print("allocate ByteBuf(9, 100)", buffer);


    }

    @Test
    public void testUnpooledDirectByteBuf2() {
        System.setProperty("io.netty.noUnsafe", "true");

        UnpooledDirectByteBuf buffer =
                (UnpooledDirectByteBuf) UnpooledByteBufAllocator.DEFAULT.directBuffer(9, 100);

        print("allocate ByteBuf(9, 100)", buffer);


    }

    @Test
    public void testUnpooledDirectByteBuf3() {
        UnpooledUnsafeDirectByteBuf buffer =
                (UnpooledUnsafeDirectByteBuf)
                        UnpooledByteBufAllocator.DEFAULT.directBuffer(9, 100);
        print("allocate ByteBuf(9, 100)", buffer);
    }

    @Test
    public void testUnpooledByteBuf() {

        UnpooledHeapByteBuf buffer =
                (UnpooledHeapByteBuf) UnpooledByteBufAllocator.DEFAULT.heapBuffer(9, 100);

        print("allocate ByteBuf(9, 100)", buffer);


    }

    @Test
    public void testUnpooledUnsafeByteBuf() {
        System.setProperty("io.netty.noUnsafe", "true");

        UnpooledHeapByteBuf buffer = (UnpooledHeapByteBuf) UnpooledByteBufAllocator.DEFAULT.heapBuffer(9, 100);

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