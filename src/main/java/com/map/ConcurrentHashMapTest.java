/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/19 上午8:26
 */
package com.map;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap  多线程写性能不如Hashtable，读性能很高，读多写少
 * @author HanZhonghua
 * @version 1.0
 */
public class ConcurrentHashMapTest {

    static ConcurrentHashMap<UUID, UUID> concurrentHashMap = new ConcurrentHashMap<>();
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
                concurrentHashMap.put(key[i], value[i]);
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
        System.out.println(concurrentHashMap.size());

        long start = System.currentTimeMillis();
        for (int i = 0; i< threads.length; i++) {
            threads[i] = new Thread(()->{
                for (int j = 0; j<= 1000000; j++) {
                    concurrentHashMap.get(key[10]);
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
