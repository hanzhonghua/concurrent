/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/28 下午11:09
 */
package com.Interview;

import java.util.Arrays;
import java.util.List;

/**
 * 两个线程交替打印：A1B2C3D4E5F6...
 * 考的是线程的通信机制，Lock notify(),wait() LockSupport 。。很多实现方法
 * @author HanZhonghua
 * @version 1.0
 */
public class interview1 {

    public static void main(String[] args) {
        List<String> l1 = Arrays.asList("A", "B", "C", "D", "E", "F");
        List<Integer> l2 = Arrays.asList(1, 2, 3, 4, 5, 6);

        Object lock = new Object();

        new Thread(() -> {
            synchronized (lock) {
                for (String s : l1) {
                    System.out.print(s);
                    lock.notify();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 这个地方需要注意一下，如果不加的话，不影响程序的正确性，但是会有问题，有个线程会一直阻塞着，为什么呢？分析下
                // 首先为了保证线程可以交替执行，在当前线程打印完之后，需要先进行唤醒一下然后在阻塞（同一把锁），会导致有一个线程
                // 最后执行完唤醒 然后阻塞之后，没有再被唤醒，就导致这个线程一直阻塞了，所以在这个地方在唤醒一下
                lock.notify();
            }
        }).start();
        new Thread(() -> {
            synchronized (lock) {
                for (Integer s : l2) {
                    System.out.print(s);
                    lock.notify();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                lock.notify();
            }
        }).start();

    }
}
