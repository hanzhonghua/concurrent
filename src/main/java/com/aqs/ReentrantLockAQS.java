/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/11 下午9:48
 */
package com.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 底层也是调用的AQS（AbstractQueueSynchronizer），来实现的，主要分为公平锁非公平锁，默认是非公平锁，非公平锁性能较之更好
 * 内部接口Sync 公平锁实现FailSync  非公平锁实现NunFailSync
 * volatile + CAS实现，有一核心字段：state + CLH（双向链表）队列，state在ReentrantLock中用来维护重入的次数，CLH队列里是等待锁的线程队列(Node节点)，
 * state用
 * volatile修饰，CLH队列出队入队通过CAS实现
 * @author HanZhonghua
 * @version 1.0
 */
public class ReentrantLockAQS {

    public static void main(String[] args) {

        ReentrantLock lock = new ReentrantLock(true);
        for (int i = 0; i<2; i++) {
            new Thread(()->{
                try {
                    try {
                        // lock.tryLock(1L, TimeUnit.SECONDS);
                        lock.lock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("线程" + Thread.currentThread().getName()+"获得锁开始执行");
                    try {
                        Thread.sleep(50000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("线程" + Thread.currentThread().getName()+"执行完毕");
                } finally {
                    lock.unlock();
                }
            }).start();

        }
    }
}
