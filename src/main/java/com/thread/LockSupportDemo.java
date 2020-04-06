/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/6 下午8:33
 */
package com.thread;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport.park(); 可以让一个线程停止运行，LockSupport.unpark(t1);指定某个线程执行
 * 调用的底层Unsafe的方法
 * @author HanZhonghua
 * @version 1.0
 */
public class LockSupportDemo {

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            for (int a = 0;a<10;a++) {
                System.out.println(a);
                if (a == 5) {

                    LockSupport.park();
                }
            }
        });
        t1.start();

        Thread.sleep(1000);
        LockSupport.unpark(t1);
    }
}
