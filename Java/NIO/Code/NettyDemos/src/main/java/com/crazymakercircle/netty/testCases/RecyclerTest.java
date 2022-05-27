package com.crazymakercircle.netty.testCases;

import com.crazymakercircle.util.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.Recycler;
import org.junit.Test;

public class RecyclerTest
{

    private static class Foo {

        //回收处理实例
        private final Recycler.Handle<Foo> handle;


        private static final Recycler<Foo> RECYCLER = new Recycler<Foo>() {
            @Override
            protected Foo newObject(Handle<Foo> handle) {
                return new Foo(handle);
            }
        };

        //新建一个实例
        static Foo newInstance() {
            Foo foo = RECYCLER.get();
            return foo;
        }


        public Foo(Recycler.Handle<Foo> handle) {
            this.handle = handle;
        }
 
        public void recycle() {
            handle.recycle(this);
        }
    }

    @Test
    public void testPooledByteBuf(){
        ByteBuf foo = PooledByteBufAllocator.DEFAULT.ioBuffer(32);
        Logger.cfo("foo == "+  foo);

    }

    @Test
    public void testRecycler(){
        Foo foo = Foo.RECYCLER.get();

        foo.recycle();

        Foo foo1 =  Foo.RECYCLER.get();

        Logger.cfo("foo1 == foo ? "+ (foo1 == foo));
    }
}