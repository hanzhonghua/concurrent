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
 * ArrayList不是线程安全的，底层是数组实现的，多线程操作根据下标删除时可能删除的是同一个元素
 * 怎么改进呢？提供的工具类Vector
 * @author HanZhonghua
 * @version 1.0
 */
public class SellTicketArrayList {

    static List<Integer> list = new ArrayList<>();

    static {
        for (int i = 0; i< 1000; i++) {
            list.add(i);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Thread threads[] = new Thread[10];
        Set s = new HashSet<>();
        Set<Integer> objects = Collections.synchronizedSet(s);
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                while (list.size() > 0) {
                    Integer remove = list.remove(0);
                    if (objects.contains(remove)) {
                        System.out.println("哇哦");
                    }
                    objects.add(remove);
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
