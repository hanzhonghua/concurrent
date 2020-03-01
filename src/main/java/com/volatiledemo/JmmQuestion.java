/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/3/1 下午3:48
 */
package com.volatiledemo;

/**
 * 关于JMM Happens-Before规则的思考题，有一个共享变量a，在一个线程内设置了a=3，那么有几种方法可以让这个操作在其它线程看到
 * 1.a=3操作加锁
 * 2.start方法 A线程调用B.start，那么A在start之前的操作对B是可见的
 * 3.join方法 A调用b.join，A等待B执行完成，B线程操作对于A是可见的
 * 4.原子类
 * @author HanZhonghua
 * @version 1.0
 */
public class JmmQuestion {

    private int a = 0;

    // 1 操作加锁
    synchronized void add() {
        a = 3;
    }

    public static void main(String[] args) throws InterruptedException {
        JmmQuestion jq = new JmmQuestion();
        /*jq.add();
        new Thread(()->{
            System.out.println(jq.a);
        }).start();*/

        // 2. start规则
        /*Thread t1 = new Thread(() -> {
            System.out.println(jq.a);
        });
        jq.add();
        t1.start();*/

        // 3.join规则
        Thread t1 = new Thread(() -> {
            jq.add();
        });
        t1.start();
        t1.join();
        System.out.println(jq.a);
    }
}
