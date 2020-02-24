/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/2/16 上午9:48
 */
package com.thread;

import java.util.concurrent.CountDownLatch;

/**
 * JDK内置锁，synchronized，多个线程存在写操作同一个共享变量，为了保证线程安全，需要对这个变量加锁
 * 所谓线程按照的拿个对象来说就是在多线程环境下和单线程环境下表现出来的现象一样
 * 不能使用常量作为锁对象
 * @author HanZhonghua
 * @version 1.0
 */
public class SynchronizedTest {

    private int count = 0;
    private final Object o = new Object();

    /**
     * 不加锁访问，多线程访问不安全，每个线程都有自己的缓存区，线程操作时操作的是自己缓存区中的变量，这个时候可能其它的线程
     * 已经再自己的缓存区把变量改掉了，但是这个线程不知道，所有会出现线程安全问题，解决方案就是加锁
     */
    public void deduct() {
        count++;
    }

    // 方法级别相当于this
    public synchronized void deduct1() {
        count++;
    }

    // 对象级锁
    public void deduct2() {
        synchronized (o) {
            count++;
        }
    }

    public void deduct3() {
        synchronized (this) {
            count++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
            final SynchronizedTest test = new SynchronizedTest();

            for (int i=0;i<100;i++) {

                new Thread(new Runnable() {
                    public void run() {
                        test.deduct();
                        System.out.println(test.count);
                    }
                }).start();
        }
    }
}
