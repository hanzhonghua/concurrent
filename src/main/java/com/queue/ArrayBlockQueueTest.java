/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/19 下午10:11
 */
package com.queue;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * ArrayBlockingQueue 名称可以看出，底层是数组实现，ReentrantLock公平锁
 * 线程安全的Queue
 * @author HanZhonghua
 * @version 1.0
 */
public class ArrayBlockQueueTest {

    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

        for (int i = 0; i< 10; i++) {
            // 底层是offer，ReentrantLock锁，如果队列满了，throw queue full Exception
            queue.add(i+"");
        }
        // 等待通知机制，如果队列满了，则等待，
        queue.put("3");
        // 阻塞获取堆首元素，不删除，底层使用ReentrantLock锁
        String peek = queue.peek();
        // 阻塞获取堆首元素，删除，底层使用ReentrantLock锁
        String poll = queue.poll();
        // ReentrantLock的等待通知机制，阻塞获取堆首元素，删除
        String take = queue.take();
        queue.add("2");
    }
}
