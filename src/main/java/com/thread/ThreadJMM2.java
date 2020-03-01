/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/3/1 下午2:55
 */
package com.thread;

/**
 * JMM内存模型 Happens-Before原则：线程A的调用B.start()那么A的操作Happens-Before B线程任意操作
 * 就是指如果在A线程中启动B线程，那么在B启动前，A的所有操作对应B都是可见的
 * @author HanZhonghua
 * @version 1.0
 */
public class ThreadJMM2 {

    private int a = 1;

    public static void main(String[] args) {
        ThreadJMM2 jmm2 = new ThreadJMM2();
        Thread t1 = new Thread(()->{
            System.out.println(jmm2.a);
        });
        jmm2.a = 2;
        t1.start();
    }
}
