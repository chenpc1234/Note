package com.crazymakercircle.netty.testCases;

import com.crazymakercircle.util.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.Test;
import sun.misc.Cleaner;
import sun.misc.Unsafe;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MemoryDemo
{
    static
    {
        // -Dio.netty.leakDetectionLevel=paranoid
        System.setProperty("io.netty.leakDetectionLevel", "paranoid");
    }

    @Test
    public void testBufferLeaks() throws InterruptedException
    {
        for (int i = 0; i < 500000; ++i)
        {
            ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer(1024);
//            byteBuf.release();
            byteBuf = null;

        }
        System.gc();
        Thread.sleep(1000);
    }

    @Test
    public void testPooledByteBufAllocator()
    {
        int chunkSize = 16 * 1024 * 1024;
        int pageSize = chunkSize / 2048;

        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(pageSize * 2);

        buffer.release();
    }

    @Test
    public void testUnsafe() throws Exception
    {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);

        long a = unsafe.allocateMemory(1024);
        long b = unsafe.allocateMemory(2048);
//        unsafe.reallocateMemory(a, 1024);
//        unsafe.reallocateMemory(b, 1024);
        unsafe.freeMemory(a);
        unsafe.freeMemory(b);
    }

    private static List<ByteBuffer> list = new ArrayList<>();

    private static int MB = 1024 * 1024;

    @Test
    public void testDirectBufferOOM() throws Exception
    {
//        -Xmx100M
        long maxMemory = Runtime.getRuntime().maxMemory();
        long maxDirectMemory = sun.misc.VM.maxDirectMemory();
        System.out.println("Runtime.getRuntime().maxMemory() " + (double) maxMemory / (double) 1024 / (double) 1024 + " MB");
        System.out.println("VM.maxDirectMemory() " + (double) maxDirectMemory / (double) 1024 / (double) 1024 + " MB");

        long counter = 0;
        while (true)
        {
            try
            {
                ByteBuffer buffer = ByteBuffer.allocateDirect(MB * 1);
                list.add(buffer);
                counter++;
                //删除一个 ByteBuffer 对象后，可以继续
//                if (list.size() > 99) {
//                    list.remove(0);
//                    System.out.println("counter: " + counter);
//                }
            } catch (OutOfMemoryError error)
            {
                System.out.println("counter: " + counter + " MB");
                throw error;
            }
        }
    }

    @Test
    public void testMaxDirectMemorySize() throws Exception
    {

        long maxDirectMemory = sun.misc.VM.maxDirectMemory();
        System.out.println("VM.maxDirectMemory() "
                + (double) maxDirectMemory / (double) 1024 / (double) 1024 + " MB");

        System.out.println("================================");
        ByteBuffer buffer = ByteBuffer.allocateDirect(0);
        Class<?> c = Class.forName("java.nio.Bits");
        Field maxMemory = c.getDeclaredField("maxMemory");
        maxMemory.setAccessible(true);
        synchronized (c)
        {
            Long maxMemoryValue = (Long) maxMemory.get(null);
            System.out.println("maxMemoryValue:" + (double) maxMemoryValue / (double) 1024 / (double) 1024 + " MB");
        }
    }


    @Test
    public void testGCDirector() throws Exception
    {

//        -XX:MaxDirectMemorySize=1024M   -XX:+UseConcMarkSweepGC    -XX:+PrintGCDetails
        long maxDirectMemory = sun.misc.VM.maxDirectMemory();
        System.out.println("VM.maxDirectMemory() "
                + (double) maxDirectMemory / (double) 1024 / (double) 1024 + " MB");
        ByteBuffer buffer = null;
        try
        {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 100; i++)
            {
                buffer = ByteBuffer.allocateDirect(1024 * 1024 * 250);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("程序运行时间： " + (endTime - startTime) + "ms");

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Test
    public void testGCDirector2() throws Exception
    {
        //        -XX:MaxDirectMemorySize=1024M   -XX:+UseConcMarkSweepGC    -XX:+PrintGCDetails
        long maxDirectMemory = sun.misc.VM.maxDirectMemory();
        System.out.println("VM.maxDirectMemory() " + (double) maxDirectMemory / (double) 1024 / (double) 1024 + " MB");
        ByteBuffer buffer = null;
        try
        {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++)
            {
                buffer = ByteBuffer.allocateDirect(16);
                if (i % 100 == 0)
                {
                    System.gc();
                }
            }
            long endTime = System.currentTimeMillis();
            System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testDirectByteBuffer()
    {
        ByteBuffer intBuffer = ByteBuffer.allocateDirect(20);

        for (byte i = 0; i < 5; i++)
        {
            intBuffer.put(i);
        }
        intBuffer.flip();
        for (int i = 0; i < 5; i++)
        {
            byte value = intBuffer.get();
            Logger.debug("value = " + value);
        }
    }

    //使用Cleaner手动来回收内存
    @Test
    public void testAllocateDirector() throws Exception
    {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        Field cleanerField = buffer.getClass().getDeclaredField("cleaner");
        cleanerField.setAccessible(true);
        Cleaner cleaner = (Cleaner) cleanerField.get(buffer);
        cleaner.clean();

    }

    //实际的数据对象，引用持有的实际对象
    class Data
    {
        private int id = 0;

        public int getId()
        {
            return id;
        }

        @Override
        public String toString()
        {
            return "Data(" + id + ")";
        }
    }

    //在使用弱引用时，一般会自定义其子类，加入一些自己的数据，如果本例
//加了一个id域，这里为了后续输出方便，重写了其toString方法
    class FooRef extends WeakReference<Data>
    {

        private int id;

        public FooRef(Data referent, ReferenceQueue<Object> queue)
        {
            super(referent, queue);
            //弱引用的id域初始化为被引用对象的id
            this.id = referent.getId();
        }

        @Override
        public String toString()
        {
            return "FooRef(" + id + ")";
        }

    }

    //gc之后弱引用没有被回收，因为d还指向Data对象，即还有强引用
    @Test
    public void testReferenceQueue() throws Exception
    {
        //实例化一个引用队列
        ReferenceQueue<Object> queue = new ReferenceQueue<Object>();
        //弱引用持有的实际对象
        Data d = new Data();
        //实例化一个自定义弱引用，传入实际对象和引用队列
        FooRef ref = new FooRef(d, queue);
        //显示触发gc
        System.gc();

        System.out.println("After call gc(): ");
        System.out.println("ref.get: " + ref.get());
        System.out.println("queue.poll: " + queue.poll());


    }

    //如果已经没有任何强引用指向该对象，则发生GC时会回收弱引用持有的实际对象，弱引用自身也会被放入引用队列中：
    @Test
    public void testReferenceQueue2() throws Exception
    {
        //实例化一个引用队列
        ReferenceQueue<Object> queue = new ReferenceQueue<Object>();
        //弱引用持有的实际对象
        Data d = new Data();
        //实例化一个自定义弱引用，传入实际对象和引用队列
        FooRef ref = new FooRef(d, queue);
        //垃圾回收之前删除所有的强引用
        d = null;

        //显示触发gc
        System.gc();

        System.out.println("After call gc(): ");
        System.out.println("ref.get: " + ref.get());
        System.out.println("queue.poll: " + queue.poll());

    }

    //在发生GC之前，调用clear方法手动清空弱引用持有的对象，发生GC后，弱引用也不会被放入引用队列中：
    @Test
    public void testReferenceQueue3() throws Exception
    {
        //实例化一个引用队列
        ReferenceQueue<Object> queue = new ReferenceQueue<Object>();
        //弱引用持有的实际对象
        Data d = new Data();
        //实例化一个自定义弱引用，传入实际对象和引用队列
        FooRef ref = new FooRef(d, queue);

        //发生GC前手动清空弱引用持有的实际对象
        ref.clear();

        //显示触发gc
        System.gc();

        System.out.println("After call gc(): ");
        System.out.println("ref.get: " + ref.get());
        System.out.println("queue.poll: " + queue.poll());

    }
}