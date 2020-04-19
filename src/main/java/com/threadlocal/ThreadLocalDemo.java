/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/12 下午10:33
 */
package com.threadlocal;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程本地化，内部维护了一个map，key是当前线程，是线程私有的，这个Map：ThreadLocalMap维护的Entity继承自WeakReference（弱引用，GC时候就回收）
 * 强引用 引用的生命周期没有执行完毕，即使OOM也不会回收引用指向的对象占用的内存 A a = new A();
 * 软引用 当内存不足时，回收 SoftReference
 * 弱引用 GC时候既回收 WeakReference
 * 虚引用 用于回收堆外内存
 *
 * @author HanZhonghua
 * @version 1.0
 */
public class ThreadLocalDemo {

    ThreadLocal<String> threadLocal = new ThreadLocal<>();
    Map<String, Object> map = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        ThreadLocalDemo demo = new ThreadLocalDemo();
        new Thread(()->{
            demo.threadLocal.set("小李子");
            System.out.println(demo.threadLocal.get());
            demo.map.put("key", "value");
        }).start();

        Thread.sleep(100);
        new Thread(()->{
            System.out.println(demo.threadLocal.get());
            System.out.println(demo.map.get("key"));
        }).start();
        demo.threadLocal.remove();
    }
}
