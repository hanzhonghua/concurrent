/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/4 下午9:09
 */
package com.countdownlatch;

/**
 * @author HanZhonghua
 * @version 1.0
 */
public class CownLacthByThread {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("t1开始执行");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t1执行完毕");
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("t2开始执行");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t2执行完毕");
            }
        });

        System.out.println("主线程开始执行，等待其它线程执行");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("执行完成");
    }
}
