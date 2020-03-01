/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/3/1 下午2:21
 */
package com.thread;

/**
 * Happens-Before原则：锁的加锁操作Happens-Before对这个锁的解锁
 * 如下代码，线程A在更新a=2之后，线程B在进入代码块时，一定可以看到线程A的修改
 * @author HanZhonghua
 * @version 1.0
 */
public class SynchronizedJMM1 {

    public static void main(String[] args) throws InterruptedException {
        int a = 1;
        final SynchronizedJMM1 s = new SynchronizedJMM1();
        synchronized (s) {
            a = 2;
        }
        int finalA = a;
        new Thread(() -> {
            System.out.println(finalA);
        }).start();

    }
}
