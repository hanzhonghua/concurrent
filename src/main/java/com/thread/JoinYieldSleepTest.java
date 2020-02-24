/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/2/16 下午6:15
 */
package com.thread;

/**
 * @author HanZhonghua
 * @version 1.0
 */
public class JoinYieldSleepTest {

    public static void main(String[] args) {
        //testSleep();
        //testYield();
        testJoin();
    }

    // Thread.sleep 线程休眠一会，不释放锁
    public static void testSleep() {
        for (int a = 0; a < 5; a++) {
            int i = a;
            new Thread(new Runnable() {
                public void run() {
                    System.out.println(Thread.currentThread().getName() + " 执行");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            },"T"+i).start();
        }
    }

    // Thread.yield让让线程让出正在使用的处理器，也可以不让。业务代码中不建议使用
    static void testYield() {
        new Thread(()->{
            for(int i=0; i<100; i++) {
                System.out.println("A" + i);
                if(i%10 == 0) Thread.yield();


            }
        }).start();

        new Thread(()->{
            for(int i=0; i<100; i++) {
                System.out.println("------------B" + i);
                if(i%10 == 0) Thread.yield();
            }
        }).start();
    }

    /**
     * new Thread().join()
     * 在线程A中调用B线程的join方法，会等到B线程执行完毕A线程才会继续执行
      */
    static void testJoin() {
        Thread t1 = new Thread(()->{
            for (int a = 0; a<=50; a++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("A" +a);
            }
        });
        Thread t2 = new Thread(()->{
            for (int a = 0; a<=50; a++) {
                try {
                    if (a == 10) {
                        t1.join();
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("B" +a);
            }
        });
        t1.start();
        t2.start();
    }
}
