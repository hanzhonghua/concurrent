/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/2/16 下午4:39
 */
package com.thread;

/**
 * synchronized锁是由jdk内部实现加锁和解锁的，当同步方法抛出异常时，jvm会释放锁
 * 多线程环境下，抛出异常后，就会有其它线程进入同步区，最终导致数据不一致等情况
 * 要小心业务中的异常
 * @author HanZhonghua
 * @version 1.0
 */
public class SynchronizedExceptionTest {

    private int count = 0;
    synchronized void incr() {
        System.out.println(Thread.currentThread().getName()+" 开始");
        for(;;) {
            count++;
            System.out.println(Thread.currentThread().getName()+" count:"+count);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 抛异常后出释放锁，如果想不释放，catch处理
            if (count == 5) {
                int i = 1/0;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedExceptionTest t = new SynchronizedExceptionTest();
        new Thread(t::incr, "t1").start();
        Thread.sleep(3);
        new Thread(t::incr, "t2").start();
    }
}
