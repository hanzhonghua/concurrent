/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/6 下午8:46
 */
package com.thread.interview;

import java.util.ArrayList;
import java.util.List;

/**
 * 面试题：实现一个容器，一个线程依次往容器添加10个元素，当添加到第5个元素时，另外一个线程停止运行
 * 实现1：典型的wait notify场景
 * 实现2：使用CountDownLatch
 * @author HanZhonghua
 * @version 1.0
 */
public class WaitNotifyDemo {

    public static void main(String[] args) throws InterruptedException {
        Container container = new Container();

        Object lock = new Object();
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("线程1开始");
                for (int a = 0; a < 10; a++) {
                    container.add(a + "");
                    System.out.println("add："+a);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (a==4) {
                        // 唤醒等待获取lock锁的线程2
                        lock.notify();
                        try {
                            // 因为notify不会释放锁，所有在这里wait一下，释放锁，线程2才可以获取锁执行
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    System.out.println("线程2开始");
                    if (container.size() != 5) {
                        try {
                            // 线程2判断容器元素数量不等5，等待并释放锁
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("线程停止");
                    // 这里要注意：一定要发送一下通知，因为线程1还是wait中
                    lock.notify();
                    break;
                }
            }
        });
        t2.start();
        Thread.sleep(1);
        t1.start();
    }
    static class Container {

        private List<String> collect = new ArrayList<>();
        private int count;
        public int size() {
            return count;
        }

        public void add(String s) {
            collect.add(s);
            count++;
        }
    }
}
