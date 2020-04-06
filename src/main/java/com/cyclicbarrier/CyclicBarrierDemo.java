/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/5 上午10:13
 */
package com.cyclicbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.RandomUtils;

/**
 * 可以实现多个线程互相等待，都准备好了一块执行，好比出去旅游，人都齐了才可以出发
 * 同样是维护了一个计数器，只不过和CountDownLatch的区别是当CyclicBarrier的计数器
 * 减到0时，会自动重置，提供的await()可以减计数器
 *
 * 构造方法有两个参数，第一个是计数器个数，第二个是计数器为0时执行的回调函数
 * @author HanZhonghua
 * @version 1.0
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(5, ()->{
            System.out.println("都准备好了，出发");
        });
        barrier.reset();
        ExecutorService service = Executors.newFixedThreadPool(5);
        while (true) {
            for (int i = 0;i<5;i++) {
                final int a = i;
                service.execute(() -> {
                    try {
                        Thread.sleep(RandomUtils.nextInt(1000, 9999));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(a +" 准备好了");
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        //service.shutdown();
    }
}
