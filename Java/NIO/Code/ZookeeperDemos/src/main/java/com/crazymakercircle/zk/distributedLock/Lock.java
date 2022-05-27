package com.crazymakercircle.zk.distributedLock;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
public interface Lock {
    /**
     * 加锁方法
     *
     * @return 是否成功加锁
     */
    boolean lock() throws Exception;

    /**
     * 解锁方法
     *
     * @return 是否成功解锁
     */
    boolean unlock();
}
