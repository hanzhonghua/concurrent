/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/19 上午8:26
 */
package com.map;

import java.util.Hashtable;
import java.util.UUID;

/**
 * Hashtable 早起线程安全map，所有方法都加了synchronized锁
 * 模拟多线程 写 读 Hashtable性能
 * @author HanZhonghua
 * @version 1.0
 */
public class HashtableTest {

    static Hashtable<UUID, UUID> hashtable = new Hashtable();
    static int count = Context.COUNT;
    static UUID key[] = new UUID[count];
    static UUID value[] = new UUID[count];
    static int threadCount = Context.THREAD_COUNT;

    static {
        for (int i = 0; i < count; i++) {
            key[i] = UUID.randomUUID();
            value[i] = UUID.randomUUID();
        }
    }

    static class MyThread extends Thread {

        private int start;
        // 一个线程执行的数据条数
        private int gap = count/threadCount;

        public MyThread(int start) {
            this.start = start;
        }
        @Override
        public void run() {
            for (int i = start; i < start + gap; i++) {
                hashtable.put(key[i], value[i]);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        long startTime = System.currentTimeMillis();
        Thread threads[] = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new MyThread(i * (count/threadCount));
        }

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - startTime);
        System.out.println(hashtable.size());

        long start = System.currentTimeMillis();
        for (int i = 0; i< threads.length; i++) {
            threads[i] = new Thread(()->{
                for (int j = 0; j<= 1000000; j++) {
                    hashtable.get(key[10]);
                }
            });
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        long end2 = System.currentTimeMillis();
        System.out.println(end2 - start);
    }
}
