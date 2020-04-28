/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/25 下午4:42
 */
package com.queue;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

/**
 * TransferQueue jdk1.7出的Queue 实现了BlockingQueue，BlockingQueue队列满了阻塞写入，队列空了阻塞取出
 * 而 TransferQueue 以及实现类 LinkedTransferQueue 有些不同，支持生产者一直阻塞直到添加到队列的元素被某个
 * 消费者消费 而 transfer() 方法就是实现这个功能的
 * @author HanZhonghua
 * @version 1.0
 */
public class TransferQueueTest {

    public static void main(String[] args) throws InterruptedException {
        TransferQueue<String> transferQueue = new LinkedTransferQueue<>();

        for (int i=0;i<3;i++) {
            final int a = i;
            new Thread(() -> {
                try {
                    System.out.println("start transfer");
                    transferQueue.transfer(a+"");
                    System.out.println("end transfer");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Thread.sleep(5000);

        for (int i=0;i<3;i++) {
            new Thread(() -> {
                try {
                    System.out.println("start take");
                    transferQueue.take();
                    System.out.println("end take");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
