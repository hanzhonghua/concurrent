/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/2/13 下午10:00
 */
package com.thread;

/**
 * Java里创建线程的两种方式继承Thread 实现Runnable
 * 启动可以使用start 也可以直接调用run方法，但是不要直接调用run方法，因为run方法只是一个普通方法而已，start方法是启动一个线程，会调用run方法
 * 就是说：在主线程中调用另外一个方法的start方法，主线程会继续执行，是异步的。如果直接调用run，只是同步的调用，会等到run方法执行完毕主线程才会继续
 * 执行。调用start之后并不会立即执行run方法，需要和其它线程竞争cpu，当获取cpu时间片后，才会执行run方法
 * 线程状态，内部维护了枚举，注意这只是Java维护的线程状态，和CPU内核线程还不太一样，后聊
 * new
 * runnable
 * running
 * blocking
 * time waiting
 * waiting
 * dead
 * 具体转化图见图
 * @author HanZhonghua
 * @version 1.0
 */
public class Thread1 {

    public static void main(String[] args) {
        A a = new A();
        System.out.println(0);
        a.start();
        System.out.println(1);
        a.run();
        System.out.println(2);
        B b = new B();
        new Thread(b).start();
    }
}

class A extends Thread {
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("I am a Thread A");
    }
}

class B implements Runnable {
    public void run() {
        System.out.println("I am Thread B");
    }
}
