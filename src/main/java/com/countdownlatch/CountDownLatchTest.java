/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/4 下午9:14
 */
package com.countdownlatch;

import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatch维护了一个计数器，每次调用downLatch()计数器-1，如果计数器>0，调用await()线程会被阻塞
 * 可以理解：一个线程等待其它多个线程都执行完毕之后才会执行
 * @author HanZhonghua
 * @version 1.0
 */
public class CountDownLatchTest {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch downLatch = new CountDownLatch(2);
        for (int i = 0; i < 2; i++) {
            final int a = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("线程：" + a +"开始执行");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("线程：" + a +"执行完毕");
                    downLatch.countDown();
                }
            }).start();
        }
        downLatch.await();
        System.out.println("执行完毕");
    }
}
