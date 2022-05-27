package com.crazymakercircle.zk.distributedLock;

import com.crazymakercircle.zk.ZKclient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
@Slf4j
public class ZkLock implements Lock {
    //ZkLock的节点链接
    private static final String ZK_PATH = "/test/lock";
    private static final String LOCK_PREFIX = ZK_PATH + "/";
    private static final long WAIT_TIME = 1000;
    //Zk客户端
    CuratorFramework client = null;

    private String locked_short_path = null;
    private String locked_path = null;
    private String prior_path = null;
    final AtomicInteger lockCount = new AtomicInteger(0);
    private Thread thread;

    public ZkLock() {
        ZKclient.instance.init();
        synchronized (ZKclient.instance) {
            if (!ZKclient.instance.isNodeExist(ZK_PATH)) {
                ZKclient.instance.createNode(ZK_PATH, null);
            }
        }
        client = ZKclient.instance.getClient();
    }

    @Override
    public boolean lock() {
//可重入，确保同一线程，可以重复加锁

        synchronized (this) {
            if (lockCount.get() == 0) {
                thread = Thread.currentThread();
                lockCount.incrementAndGet();
            } else {
                if (!thread.equals(Thread.currentThread())) {
                    return false;
                }
                lockCount.incrementAndGet();
                return true;
            }
        }

        try {
            boolean locked = false;
//首先尝试着去加锁
            locked = tryLock();

            if (locked) {
                return true;
            }
            //如果加锁失败就去等待
            while (!locked) {

                await();

                //获取等待的子节点列表

                List<String> waiters = getWaiters();
//判断，是否加锁成功
                if (checkLocked(waiters)) {
                    locked = true;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            unlock();
        }

        return false;
    }

    /**
     * 释放锁
     *
     * @return 是否成功释放锁
     */
    @Override
    public boolean unlock() {
//只有加锁的线程，能够解锁
        if (!thread.equals(Thread.currentThread())) {
            return false;
        }
//减少可重入的计数
        int newLockCount = lockCount.decrementAndGet();
//计数不能小于0
        if (newLockCount < 0) {
            throw new IllegalMonitorStateException("Lock count has gone negative for lock: " + locked_path);
        }
//如果计数不为0，直接返回
        if (newLockCount != 0) {
            return true;
        }
        //删除临时节点
        try {
            if (ZKclient.instance.isNodeExist(locked_path)) {
                client.delete().forPath(locked_path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void await() throws Exception {

        if (null == prior_path) {
            throw new Exception("prior_path error");
        }

        final CountDownLatch latch = new CountDownLatch(1);


        //订阅比自己次小顺序节点的删除事件
        Watcher w = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("监听到的变化 watchedEvent = " + watchedEvent);
                log.info("[WatchedEvent]节点删除");

                latch.countDown();
            }
        };

        client.getData().usingWatcher(w).forPath(prior_path);
/*
        //订阅比自己次小顺序节点的删除事件
        TreeCache treeCache = new TreeCache(client, prior_path);
        TreeCacheListener l = new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client,
                                   TreeCacheEvent event) throws Exception {
                ChildData data = event.getData();
                if (data != null) {
                    switch (event.getType()) {
                        case NODE_REMOVED:
                            log.debug("[TreeCache]节点删除, path={}, data={}",
                                    data.getPath(), data.getData());

                            latch.countDown();
                            break;
                        default:
                            break;
                    }
                }
            }
        };

        treeCache.getListenable().addListener(l);
        treeCache.start();*/
        latch.await(WAIT_TIME, TimeUnit.SECONDS);
    }
    /**
     * 尝试加锁
     * @return 是否加锁成功
     * @throws Exception 异常
     */
    private boolean tryLock() throws Exception {
        //创建临时Znode
        locked_path = ZKclient.instance
                .createEphemeralSeqNode(LOCK_PREFIX);
        //然后获取所有节点
        List<String> waiters = getWaiters();

        if (null == locked_path) {
            throw new Exception("zk error");
        }
        //取得加锁的排队编号
        locked_short_path = getShortPath(locked_path);

        //获取等待的子节点列表，判断自己是否第一个
        if (checkLocked(waiters)) {
            return true;
        }

        // 判断自己排第几个
        int index = Collections.binarySearch(waiters, locked_short_path);
        if (index < 0) { // 网络抖动，获取到的子节点列表里可能已经没有自己了
            throw new Exception("节点没有找到: " + locked_short_path);
        }

        //如果自己没有获得锁，则要监听前一个节点
        prior_path = ZK_PATH + "/" + waiters.get(index - 1);

        return false;
    }

    private String getShortPath(String locked_path) {

        int index = locked_path.lastIndexOf(ZK_PATH + "/");
        if (index >= 0) {
            index += ZK_PATH.length() + 1;
            return index <= locked_path.length() ? locked_path.substring(index) : "";
        }
        return null;
    }

    private boolean checkLocked(List<String> waiters) {

        //节点按照编号，升序排列
        Collections.sort(waiters);

        // 如果是第一个，代表自己已经获得了锁
        if (locked_short_path.equals(waiters.get(0))) {
            log.info("成功的获取分布式锁,节点为{}", locked_short_path);
            return true;
        }
        return false;
    }


    /**
     * 从zookeeper中拿到所有等待节点
     */
    protected List<String> getWaiters() {

        List<String> children = null;
        try {
            children = client.getChildren().forPath(ZK_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return children;

    }


}
