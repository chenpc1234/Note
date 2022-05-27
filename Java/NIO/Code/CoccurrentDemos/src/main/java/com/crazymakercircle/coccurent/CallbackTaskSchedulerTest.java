package com.crazymakercircle.coccurent;

import com.crazymakercircle.cocurrent.CallbackTask;
import com.crazymakercircle.cocurrent.CallbackTaskScheduler;
import com.crazymakercircle.util.Logger;
import com.crazymakercircle.util.ThreadUtil;
import org.junit.Test;


public class CallbackTaskSchedulerTest {

    @Test
    public void testfallback() {
        //异步任务，处理登录的逻辑
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                throw new RuntimeException("aaa");
            }
            //异步任务返回
            @Override
            public void onBack(Boolean r) {

                Logger.info("成功了");
            }
            //异步任务异常
            @Override
            public void onException(Throwable t) {
                t.printStackTrace();
            }
        });
        ThreadUtil.sleepMilliSeconds(Integer.MAX_VALUE);
    }
    @Test
    public void testOK() {
        //异步任务，处理登录的逻辑
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
              return true;
            }
            //异步任务返回
            @Override
            public void onBack(Boolean r) {

                Logger.info("成功了");
            }
            //异步任务异常
            @Override
            public void onException(Throwable t) {
                t.printStackTrace();
            }
        });
        ThreadUtil.sleepMilliSeconds(Integer.MAX_VALUE);
    }

}
