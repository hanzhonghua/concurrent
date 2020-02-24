/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/2/16 下午2:38
 */
package com.thread;

/**
 * 1.同步方法和非同步方法可以由不同线程同时调用
 * 2.synchronized是可重入的，一个线程获得一个对象锁，会有一个计算器，维护在对象头里，记录的有线程编号，一个线程获得锁执行之后，这个线程
 *  对应的计数器+1，在退出方法时计数器-1。计数器为0时其它线程可以获得这个对象的锁，如果同一个线程多次调用同一把锁包含的方法，计数器累加
 * @author HanZhonghua
 * @version 1.0
 */
public class SynchronizedReentrantTest {

    public synchronized void m1() {
        System.out.println("当前线程："+Thread.currentThread().getName()+" m1 start");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("当前线程："+Thread.currentThread().getName()+" m1 end");
    }

    public void m2() {
        System.out.println("当前线程："+Thread.currentThread().getName()+" m2 start");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("当前线程："+Thread.currentThread().getName()+" m2 end");
    }

    public synchronized void m3() {
        System.out.println("当前线程："+Thread.currentThread().getName()+" m3 start");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        m4();
        System.out.println("当前线程："+Thread.currentThread().getName()+" m3 end");
    }

    public synchronized void m4() {
        System.out.println("当前线程："+Thread.currentThread().getName()+" m4 start");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("当前线程："+Thread.currentThread().getName()+" m4 end");
    }

    public synchronized void m5() {
        System.out.println("当前线程："+Thread.currentThread().getName()+" father m5 start");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("当前线程："+Thread.currentThread().getName()+" father m5 end");
    }

    public static void main(String[] args) {
        SynchronizedReentrantTest test = new SynchronizedReentrantTest();
        // 同步和非同步方法可以同时调用
        //new Thread(()->test.m1(),"t1").start();
        //new Thread(()->test.m2(),"t1").start();

        // 重入性
        // new Thread(()->test.m3(),"t1").start();

        // 重入 - 父子
        Son son = new Son();
        new Thread(son::m5).start();

    }

    static class Son extends SynchronizedReentrantTest {
        @Override
        public synchronized void m5() {
            System.out.println("当前线程："+Thread.currentThread().getName()+" son m5 start");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            super.m5();
            System.out.println("当前线程："+Thread.currentThread().getName()+" son m5 end");
        }
    }
}
