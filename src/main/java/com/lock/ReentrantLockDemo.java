/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/1/12 下午4:16
 */
package com.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HanZhonghua
 * @version 1.0
 * 可重入锁：当一个线程请求一个由其它线程持有的锁时，发出请求的线程会被阻塞。但是在重入锁中，如果一个线程获取已经由它持有的锁，是可以请求成功的，这就是可重入锁，
 * 重入的粒度是线程级别的
 * ReentrantLock和synchronized都是可重入锁
 * synchronized jvm会释放锁，而ReentrantLock需要手动释放锁
 */
public class ReentrantLockDemo {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        for (int a = 0;a<=5; a++) {
            try {
                lock.lock();
                Thread.sleep(1000);
                System.out.println(a);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}
