package com.crazymakercircle.pool;

import com.crazymakercircle.util.Logger;
import com.google.common.util.concurrent.*;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 尼恩 at 疯狂创客圈
 */

public class CounDownDemo {

    public static final int SLEEP_GAP = 500;


    public static String getCurThreadName() {
        return Thread.currentThread().getName();
    }

    static class HotWaterJob implements Callable<Boolean> //①
    {

        @Override
        public Boolean call() throws Exception //②
        {

            try {
                Logger.tcfo("洗好水壶");
                Logger.tcfo("灌上凉水");
                Logger.tcfo("放在火上");

                //线程睡眠一段时间，代表烧水中
                Thread.sleep(SLEEP_GAP);
                Logger.tcfo("水开了");

            } catch (InterruptedException e) {
                Logger.tcfo(" 发生异常被中断.");
                return false;
            }
            Logger.tcfo(" 运行结束.");

            return true;
        }
    }

    static class WashJob implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {


            try {
                Logger.tcfo("洗茶壶");
                Logger.tcfo("洗茶杯");
                Logger.tcfo("拿茶叶");
                //线程睡眠一段时间，代表清洗中
                Thread.sleep(SLEEP_GAP);
                Logger.tcfo("洗完了");

            } catch (InterruptedException e) {
                Logger.tcfo(" 清洗工作 发生异常被中断.");
                return false;
            }
            Logger.tcfo(" 清洗工作  运行结束.");
            return true;
        }

    }


    public static void main(String args[]) {

        Callable<Boolean> hJob = new HotWaterJob();//③

        Callable<Boolean> wJob = new WashJob();//③

        ExecutorService jPool =
                Executors.newFixedThreadPool(10);

        CountDownLatch countDownLatch = new CountDownLatch(2);
        ListeningExecutorService gPool =
                MoreExecutors.listeningDecorator(jPool);

        ListenableFuture<Boolean> hFuture = gPool.submit(hJob);

        Futures.addCallback(hFuture, new FutureCallback<Boolean>() {
            public void onSuccess(Boolean r) {
                if (!r) {
                    Logger.tcfo("烧水失败，没有茶喝了");

                } else {

                    countDownLatch.countDown();
                }

            }

            public void onFailure(Throwable t) {
                Logger.tcfo("烧水失败，没有茶喝了");
            }
        });


        ListenableFuture<Boolean> wFuture = gPool.submit(wJob);

        Futures.addCallback(wFuture, new FutureCallback<Boolean>() {
            public void onSuccess(Boolean r) {
                if (!r) {
                    Logger.tcfo("杯子洗不了，没有茶喝了");
                } else {

                    countDownLatch.countDown();

                }
            }

            public void onFailure(Throwable t) {
                Logger.tcfo("杯子洗不了，没有茶喝了");
            }
        });

        try {
            synchronized (countDownLatch) {
                countDownLatch.await(5000, TimeUnit.MICROSECONDS);

            }
            Thread.currentThread().setName("主线程");


            Logger.tcfo("泡茶喝");


        } catch (InterruptedException e) {
            Logger.tcfo(getCurThreadName() + "发生异常被中断.");
        }
        Logger.tcfo(getCurThreadName() + " 运行结束.");

        gPool.shutdown();

    }


}