/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/6 下午9:37
 */
package com.thread.interview;

import java.util.ArrayList;
import java.util.List;

/**
 * 生产者消费者，2个线程生成，10个线程消费 使用synchronized实现
 * 维护一个容器，装10个元素，提供get add 方法，get拿走一个元素，容器为空消费者阻塞唤醒生成者生成，add添加一个元素，容器已满，生产者阻塞唤醒消费者消费
 * @author HanZhonghua
 * @version 1.0
 */
public class ProducerConsumerSync {

    private List<String> list = new ArrayList<>();
    private static final int MAX = 10;

    public synchronized void add(String s) {
        if (list.size() == MAX) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        list.add(s);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.notifyAll();
    }

    public synchronized String get() {
        // 这里一定是用while 不可以使用 if
        while (list.size() == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String remove = list.remove(0);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.notifyAll();
        return remove;
    }


    public static void main(String[] args) {
        ProducerConsumerSync p = new ProducerConsumerSync();

        for (int i = 0; i< 10; i++) {
            new Thread(()->{
                while (true) {
                    String s = p.get();
                    System.out.println("线程："+Thread.currentThread().getName() + " 取元素："+s);
                }
            }).start();

        }

        for (int i = 0; i < 2; i++) {
            final int a = i;
            new Thread(()->{
                while (true) {
                    p.add(a+"");
                    System.out.println("线程："+Thread.currentThread().getName() + " 存元素："+a);
                }
            }).start();
        }

    }
}


