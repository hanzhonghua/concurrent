/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/1/13 下午1:00
 */
package com.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HanZhonghua
 * @version 1.0
 * ReentrantLock模拟阻塞队列，对应readme.md文件描述的管程
 */
public class LockBlockingQueueDemo {

    public static void main(String[] args) {

        final LockBlockQueue queue = new LockBlockQueue();
        new Thread() {
            public void run() {
                for (;;) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int i = new Random().nextInt(10);
                    System.out.println("线程："+Thread.currentThread().getName()+"写入缓存数据：" +i);
                    queue.set(i+"");
                }
            }
        }.start();
        new Thread() {
            public void run() {
                for (;;) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String s = queue.get(0);
                    System.out.println("线程："+Thread.currentThread().getName()+"取出缓存数据：" +s);
                }
            }
        }.start();

    }

    static class LockBlockQueue {

        private List<String> list = new ArrayList<String>(5);
        final ReentrantLock lock = new ReentrantLock();
        // 条件变量，队列不空
        final Condition notEmpty = lock.newCondition();
        // 条件变量，队列不满
        final Condition notFull = lock.newCondition();

        public void set(String str) {
            lock.lock();
            try {
                while (list.size() >= 5) {
                    try {
                        notFull.await();
                    } catch (InterruptedException e) {
                        System.out.println("队列已满，线程："+Thread.currentThread().getName()+"写入等待");
                    }
                }
                list.add(str);
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        public String get(int index) {
            lock.lock();
            try {
                while (list.size() <= 0) {
                    try {
                        notEmpty.await();
                    } catch (InterruptedException e) {
                        System.out.println("队列已空，线程："+Thread.currentThread().getName()+"取出等待");
                    }
                }
                String str = list.remove(index);
                notFull.signal();
                return str;
            } finally {
                lock.unlock();
            }
        }
    }
}
