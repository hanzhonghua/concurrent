/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/19 下午8:39
 */
package com.list;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 模拟10个窗口售卖1000火车票 看下段代码存在问题
 * @author HanZhonghua
 * @version 1.0
 */
public class SellTicketQueue {

    static Queue<Integer> queue = new ConcurrentLinkedQueue<>();

    static {
        for (int i = 0; i< 1000; i++) {
            queue.add(i);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Thread threads[] = new Thread[10];
        Set s = new HashSet<>();
        Set<Object> objects = Collections.synchronizedSet(s);
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                while (queue.size() > 0) {
                    Object take = queue.poll();
                    if (objects.contains(take)) {
                        System.out.println("哇哦");
                    }
                    objects.add(take);
                }
            });
        }

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }


        System.out.println("------------------");
        System.out.println(objects.size());
    }

}
