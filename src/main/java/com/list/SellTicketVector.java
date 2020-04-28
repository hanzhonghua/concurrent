/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/19 下午8:39
 */
package com.list;

import java.util.*;

/**
 * 模拟10个窗口售卖1000火车票 看下段代码存在问题
 * 工具类Vector，线程安全的
 * 但是下面代码还是有问题，为什么呢 虽然Vector.size() 和Vector.remove 都是线程安全的，但是合到一块，操作就不是线程安全的
 * 比如到最后一个元素时，线程A判断size > 0，线程B判断size > 0，但是 A 先删除了，这个时候 B 在执行删除就会抛数组下标越界异常
 * 怎么解决？当然是加锁
 * @author HanZhonghua
 * @version 1.0
 */
public class SellTicketVector {

    static List<Integer> list = new Vector<>();

    static {
        for (int i = 0; i< 1000; i++) {
            list.add(i);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        final Object o = new Object();
        Thread threads[] = new Thread[10];
        Set s = new HashSet<>();
        Set<Integer> objects = Collections.synchronizedSet(s);
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                synchronized (o) {
                    while (list.size() > 0) {
                        Integer remove = list.remove(0);
                        if (objects.contains(remove)) {
                            System.out.println("哇哦");
                        }
                        objects.add(remove);
                    }
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
