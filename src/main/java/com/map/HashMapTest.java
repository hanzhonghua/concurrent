/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/19 上午8:26
 */
package com.map;

import java.util.HashMap;
import java.util.UUID;

/**
 * HashMap 不是线程安全的，多线程写，容易造成循环链表
 * @author HanZhonghua
 * @version 1.0
 */
public class HashMapTest {

    static HashMap<UUID, UUID> map = new HashMap<>();
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
                map.put(key[i], value[i]);
            }
        }
    }

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();
        Thread threads[] = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new MyThread(i * (count/threadCount));
        }

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end - startTime);
        System.out.println(map.size());

    }
}
