/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/1/12 下午4:16
 */
package com.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HanZhonghua
 * @version 1.0
 * ReentrantLock 可以尝试加锁，加锁不成功，可以去做其它事情，不用一直等待
 */
public class ReentrantLockDemo2 {

    Lock lock = new ReentrantLock();
    void m1 () throws InterruptedException {
        try {
            lock.lock();
            for (int a = 0; a< 3; a++) {
                Thread.sleep(1000);
                System.out.println(a);
            }
        } finally {
            lock.unlock();
        }
    }

    void m2 () throws InterruptedException {
        boolean lockFlag = false;
        try {
            lockFlag = lock.tryLock(1, TimeUnit.SECONDS);
            System.out.println(lockFlag);
        } finally {
            if (lockFlag) {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        ReentrantLockDemo2 r = new ReentrantLockDemo2();
        new Thread(()-> {
            try {
                r.m1();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(()-> {
            try {
                r.m2();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
