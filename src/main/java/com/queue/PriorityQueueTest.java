/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/19 下午10:54
 */
package com.queue;

import java.util.PriorityQueue;

/**
 * 优先级队列，默认使用的Comparable.compareTo() 自然排序
 * 也可以自定义比较器实现Comparator 接口
 * @author HanZhonghua
 * @version 1.0
 */
public class PriorityQueueTest {

    public static void main(String[] args) {
        PriorityQueue<String> queue = new PriorityQueue<>();
        queue.add("a");
        queue.add("c");
        queue.add("e");
        queue.add("b");
        queue.add("d");
        queue.add("f");
        queue.add("e");

        System.out.println(queue.size());
        for (int i = 0;i<7;i++) {
            System.out.println(queue.poll());
        }
    }
}
