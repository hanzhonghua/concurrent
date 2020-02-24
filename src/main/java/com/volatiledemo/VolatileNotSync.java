/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/2/23 下午4:34
 */
package com.volatiledemo;

/**
 * volatile并不能保证原子性，就是在多个线程修改同一个变量的时候，volatile并不能保证
 * 结果的正确性。count++在开篇readme文件已经说过，在CPU级别是分3个指令的
 * 1.内存读取count值到线程工作内存，2工作内存操作更新值+1，写回主内存，这个读取和写会的
 * 操作两个线程之间是不可见的，就有可能线程A读取到0然后+1，线程B读取0也+1，最终导致结果
 * 与理想情况不一致。这种情况可以加锁
 * volatile用途：一个线程写，多个线程读的场景
 * @author HanZhonghua
 * @version 1.0
 */
public class VolatileNotSync {

    /*volatile*/private int count = 0;
    private /*synchronized*/ void method() {
        for (int a=0;a<10000;a++) {
            count++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        VolatileNotSync demo = new VolatileNotSync();
        Thread t1 = new Thread(demo::method);
        Thread t2 = new Thread(demo::method);
        t1.start();
        t2.start();
        // 让主线程等待线程t1,和t2执行完毕
        t1.join();
        t2.join();
        System.out.println(demo.count);
    }
}
