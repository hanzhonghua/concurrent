/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/1/12 下午4:16
 */
package com.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HanZhonghua
 * @version 1.0
 * ReentrantLock 可中断的
 * thread.interrupt 可中断正在等待中的线程（wait/join/sleep）
 * 注意不能中断运行中的线程，中断就是给线程一个中断标记，运行中的线程不会检查这个中的标记，而等待/休眠中的线程会检查
 *
 *
 */
public class ReentrantLockInterrupt {

    public static void main(String[] args) throws InterruptedException {

        /*ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(()->{
            try {
                lock.lock();
                //lock.lockInterruptibly();
                System.out.println("t1启动");
                Thread.sleep(Integer.MAX_VALUE);
                System.out.println("t2 end");
            } catch (InterruptedException e) {
                System.out.println("t1 interrupted");
            } finally {
                lock.unlock();
            }
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            try {
                // 没有获得锁时，无视中断
                //lock.lock();
                // 获取锁，不过是优先响应中断，如果一直等待获得锁，则是优先响应中断
                lock.lockInterruptibly();
                System.out.println("t2 start");
                Thread.sleep(5);
                System.out.println("t2 end");
            } catch (InterruptedException e) {
                System.out.println("t2 interrupted");
            } finally {
                lock.unlock();
            }
        });
        t2.start();
        Thread.sleep(5);
        t1.interrupt();
        // 线程中断标记，lockInterruptibly方法都是可以响应中断的
        // 如果处于wait sleep状态的线程，被interrupt之后，状态会变为Runnable，然后抛出InterruptedException异常
        // t2.interrupt();

        if (t1.isInterrupted()) {
            System.out.println("断断");
        }*/

        Thread t1 = new Thread() {
            @Override
            public void run() {
                /*try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                for (;;) {
                    System.out.println(222);
                }
            }
        };
        t1.start();
        t1.interrupt();
        System.out.println(1);
    }
}
