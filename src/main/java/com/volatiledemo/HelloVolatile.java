/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/2/20 下午10:38
 */
package com.volatiledemo;

/**
 * volatile 保证了可见性与禁止指令重排序，但是保证不了原子性，使用场景是一个线程写多个线程读的场景
 * 在主内存中，写一个变量的值，当一个线程去更新这个值时，会将这个值拷贝到当前线程主内存一份，以后不在操作
 * 主内存的值，线程会不定期往主内存中同步这个值。
 * 所以如下代码中，running是存在主内存中的，当没有volatile修饰时，开启的线程调用方法method，虽然主线程
 * 把running值更新了，但是执行method的线程读取的还是线程内存空间的值，所以方法不会停止。加上volatile时
 * 就是强制让线程去读取主内存也就是堆内存的值，这个时候while循环也就结束了
 * 但是volatile保证不了原子性，也就是多个线程同时写这个变量时，还会带来不一致的问题
 * @author HanZhonghua
 * @version 1.0
 */
public class HelloVolatile {

    volatile boolean running = true;

    void method () {
        System.out.println("begin running...");
        while (running) {
        }
        System.out.println("end...");
    }

    public static void main(String[] args) throws InterruptedException {
        HelloVolatile helloVolatile = new HelloVolatile();
        /*new Thread(helloVolatile::method).start();
        Thread.sleep(1);
        helloVolatile.running = false;*/

        // 下面这段代码一定不会执行，应该主线程执行到method会无线执行下去，根本执行不到下面的代码
        helloVolatile.method();
        Thread.sleep(1);
        new Thread(() -> helloVolatile.running = false).start();

    }
}
