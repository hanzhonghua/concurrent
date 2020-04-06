/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/4 下午9:45
 */
package com.countdownlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 现在模拟需求：裁判必须等两个拳手都准备好了才可以准备吹哨让拳手开打
 * 维护的计数器，当计数器减到0时，一块执行，这个计数器可以在用一个线程中一下子减完
 *
 * await()让主线程等待其它线程执行完毕，countDown()计数器 -1
 * @author HanZhonghua
 * @version 1.0
 */
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        CountDownLatch downLatch = new CountDownLatch(2);
        // 加while主要是验证CountDownLatch的计数器不会重置，当减到0时，在减永远是0，计数器只可用一次
        while (true) {
            service.execute(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("拳手A准备好了");
                downLatch.countDown();
            });

            service.execute(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("拳手B准备好了");
                downLatch.countDown();
            });

            // 裁判（主线程必须等待拳手准备好（其它线程执行完毕））
            downLatch.await();
            System.out.println(downLatch.getCount());
            System.out.println("裁判吹哨，开打");
        }
    }
}
