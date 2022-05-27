package com.crazymakercircle.memory;

import com.crazymakercircle.util.Logger;
import com.crazymakercircle.util.ThreadUtil;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class FullGcDemo {

//  -Xms20M -Xmx20M -XX:+UseParallelGC -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(50,
            new ThreadPoolExecutor.DiscardOldestPolicy());

    public static void main(String[] args) throws Exception {

        //定时执行任务
        ThreadUtil.scheduleAtFixedRate(() -> runTask(), 100, TimeUnit.MILLISECONDS);

        ThreadUtil.sleepMilliSeconds(Integer.MAX_VALUE);
    }

    private static void runTask() {
        ThreadUtil.getMixedTargetThreadPool().submit(() -> {

            for (int i = 0; i < 1000000; i++) {
                FooEntity fooEntity = new FooEntity();
//                Logger.fo("age=" + fooEntity.age);
            }

        });
    }


}
  
