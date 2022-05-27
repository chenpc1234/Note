package com.crazymakercircle.memory;

import com.crazymakercircle.util.Logger;
import com.crazymakercircle.util.ThreadUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CyclicDependencies {
    static Scanner scanner = new Scanner(System.in);
    //声明缓存对象
    private static final Map map = new HashMap();


    /**
     * -Xms512m
     * -Xmx512m
     * -XX:-UseGCOverheadLimit
     * -XX:MaxPermSize=50m
     *
     * @param args
     */
    public static void main(String args[]) {
        Logger.fo("打开visualvm后, 回车启动 step1 ：");
        scanner.nextLine();

        //step1:循环添加对象到缓存 100W
        for (int i = 0; i < 1000000; i++) {
            FooEntity t = new FooEntity();
            map.put("key" + i, t);
        }
        Logger.fo("step1: over");

        Logger.fo("dump出堆后, 回车启动 step2 ：");
        scanner.nextLine();

        //step2:循环添加对象到缓存 100W
        for (int i = 0; i < 1000000; i++) {
            FooEntity t = new FooEntity();
            map.put("key" + i, t);
        }
        Logger.fo("step2: over");

        Logger.fo("等待10s");
        ThreadUtil.sleepSeconds(10);

        //step3:循环添加对象到缓存 300W
        for (int i = 0; i < 3000000; i++) {
            FooEntity t = new FooEntity();
            map.put("key" + i, t);
        }
        Logger.fo("step3: over");

        Logger.fo("等待10s");
        ThreadUtil.sleepSeconds(10);

        //step4:循环添加对象到缓存 300W
        for (int i = 0; i < 3000000; i++) {
            FooEntity t = new FooEntity();
            map.put("key" + i, t);
        }
        Logger.fo("step4: over");

        ThreadUtil.sleepSeconds(Integer.MAX_VALUE);
    }


}