/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/3/1 下午3:04
 */
package com.thread;

/**
 * JMM内存模型 Happens-Before原则：join等待原则，就是线程A调用B.join()，线程A等待B执行完毕，那么B的任何操作
 * Happens-Before A，就是B线程的任何操作都是对于A线程可见的
 * @author HanZhonghua
 * @version 1.0
 */
public class ThreadJMM3 {

    private int a = 1;

    public static void main(String[] args) throws InterruptedException {
        ThreadJMM3 j = new ThreadJMM3();
        Thread t1 = new Thread(()->{
            j.a = 2;
        });
        t1.start();
        t1.join();
        System.out.println(j.a);
    }
}
