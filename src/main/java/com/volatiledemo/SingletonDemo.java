/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/2/23 下午4:49
 */
package com.volatiledemo;

/**
 * 单例模式 饿汉式，懒汉式，DCL（双重检索），静态内部类
 * @author HanZhonghua
 * @version 1.0
 */
public class SingletonDemo {

    public static void main(String[] args) {
        StaticInner i = StaticInner.getInstance();
    }
}

/**
 * 饿汉式，项目启动就初始化，浪费资源
 */
class Hungry {
    private Hungry(){}
    private static Hungry instance = new Hungry();
    public static Hungry getInstance() {
        return instance;
    }
}

/**
 * 双重检索机制
 * 需要使用volatile修饰，java中，new主要分三步指令操作
 * 1.内存中申请空间M
 * 2.在开辟的内存M中初始化对象
 * 3.M内存地址赋值给instance
 * 涉及到指令重排序，可能结果变成了1，3，2，就是第一个线程在锁内执行了1，3时候，线程B开始
 * 执行，判断发现instance已经不是null了，于是返回，可能会出现NPE异常
 */
class DoubleCheck {
    private DoubleCheck(){}
    private static volatile DoubleCheck instance;

    public static DoubleCheck getInstance() {
        if (instance == null) {
            synchronized (DoubleCheck.class) {
                if (instance == null) {
                    instance = new DoubleCheck();
                }
            }
        }
        return instance;
    }
}

/**
 * 静态内部类实现单例
 * 反射也可以破坏单例模式，最安全的是枚举
 */
class StaticInner {

    private StaticInner(){}
    static StaticInner getInstance() {
        return Inner.instance;
    }

    private static class Inner {
        private static StaticInner instance = new StaticInner();
    }
}