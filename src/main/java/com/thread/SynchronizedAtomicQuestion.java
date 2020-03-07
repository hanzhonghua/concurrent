/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/3/1 下午4:14
 */
package com.thread;

/**
 * 互斥锁解决原子性问题，锁与锁保护的资源
 * synchronized主要有以下4中用法
 * @author HanZhonghua
 * @version 1.0
 */
public class SynchronizedAtomicQuestion {

    // 锁对象默认当前对象this
    synchronized void foo() {}
    // 锁相当于SynchronizedAtomicQuestion.class
    synchronized static void zoo() {}
    // 和1等价
    public void koo() {
        synchronized (this) {}
    }
    // 锁对象是obj
    Object obj = new Object();
    public void noo() {
        synchronized (obj) {}
    }

    // 看下面例子，两个方法分别++和读变量，加方法加锁，读没有加
    private int count = 0;
    synchronized void add() {
        count++;
    }
    // 管程中，只保证后续对这个锁加锁的可见性，如果不加锁，很可能拿不到正确值，比如在32位机器写long变量，如果没有保证可见性，很有可能读到写到一半的变量，是错误的
    // 如果修改成static之后，可能会有并发问题，因为static修饰的锁对象是X.class，
    // 出现了不同的锁保护资源，没有起到互斥作用
    synchronized int getCount() {
        return count;
    }

    public static void main(String[] args) {
        /*SynchronizedAtomicQuestion s = new SynchronizedAtomicQuestion();
        for (int a=0;a<100000;a++) {
            new Thread(s::add).start();
        }
        System.out.println(s.getCount());*/
        int a = 1;
        for (int i=0;i<5;i++) {
            int b = a++;
            System.out.println(b);
        }
    }
}
