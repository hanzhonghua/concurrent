/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/6 下午9:37
 */
package com.thread.interview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 面试题：实现一个容器，一个线程依次往容器添加10个元素，当添加到第5个元素时，另外一个线程停止运行
 * 实现2：使用CountDownLatch
 * 再复习下CountDownLatch，维护了计数器，await()等待，countDown计数器-1，线程调用await()阻塞，调用countDown()计数器-1，
 * 当计数器为0时，线程由阻塞变为运行状态
 * @author HanZhonghua
 * @version 1.0
 */
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        Container container = new Container();

        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);

        Thread t1 = new Thread(() -> {
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
                    latch2.countDown();
                    try {
                        latch1.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("线程1停止");
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                System.out.println("线程2开始");
                if (container.size() != 5) {
                    try {
                        latch2.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("线程2停止");
                // 这里要注意：线程1的门栓-1，线程1开始执行
                latch1.countDown();
                break;
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



