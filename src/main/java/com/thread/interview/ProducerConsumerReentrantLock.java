/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/6 下午10:56
 */
package com.thread.interview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生产者 消费者  使用ReentrantLock
 * @author HanZhonghua
 * @version 1.0
 */
public class ProducerConsumerReentrantLock {
    private List<String> list = new ArrayList<>();
    private static final int MAX = 10;
    private final ReentrantLock lock = new ReentrantLock();
    // 支持多个条件变量
    private final Condition empty = lock.newCondition();
    private final Condition full = lock.newCondition();

    public void add(String s) {

        try {
            lock.lock();
            if (list.size() == MAX) {
                try {
                    full.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            list.add(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            empty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public String get() {

        try {
            lock.lock();
            // 这里一定是用while 不可以使用 if
            while (list.size() == 0) {
                try {
                    empty.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String remove = list.remove(0);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            full.signalAll();
            return remove;
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) {
        ProducerConsumerReentrantLock p = new ProducerConsumerReentrantLock();

        for (int i = 0; i< 10; i++) {
            new Thread(()->{
                while (true) {
                    String s = p.get();
                    System.out.println("消费线程："+Thread.currentThread().getName() + " 取元素："+s);
                }
            }).start();

        }

        for (int i = 0; i < 2; i++) {
            final int a = i;
            new Thread(()->{
                while (true) {
                    p.add(a+"");
                    System.out.println("生产线程："+Thread.currentThread().getName() + " 存元素："+a);
                }
            }).start();
        }

    }
}
