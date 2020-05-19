/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/28 下午11:09
 */
package com.Interview;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 两个线程交替打印：A1B2C3D4E5F6...
 * 考的是线程的通信机制，ReentrantLock.await()  ReentrantLock.signal()
 * @author HanZhonghua
 * @version 1.0
 */
public class interview3 {

    public static void main(String[] args) {
        List<String> l1 = Arrays.asList("A", "B", "C", "D", "E", "F");
        List<Integer> l2 = Arrays.asList(1, 2, 3, 4, 5, 6);

        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Condition condition1 = lock.newCondition();

        new Thread(() -> {
            try {
                lock.lock();

                for (String s : l1) {
                        System.out.print(s);
                        condition1.signal();
                        try {
                            condition.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                condition1.signal();
            } finally {
                lock.unlock();
            }
        }).start();

        new Thread(() -> {
            try {
                lock.lock();
                for (Integer s : l2) {
                        System.out.print(s);
                        condition.signal();
                        try {
                            condition1.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                condition.signal();
            } finally {
                lock.unlock();
            }
        }).start();

    }
}
