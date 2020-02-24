/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/2/16 上午10:53
 */
package com.thread;

/**
 * run方法如果没加锁
 * @author HanZhonghua
 * @version 1.0
 */
public class SynchronizedTest2 implements Runnable {

    private int count = 100;
    public /*synchronized*/ void run() {
        count--;
        System.out.println(Thread.currentThread().getName() + " count:" +count);
    }

    public static void main(String[] args) {
        SynchronizedTest2 t = new SynchronizedTest2();
        for(int i=0; i<100; i++) {
            new Thread(t,"Thread"+i).start();
        }
    }
}
