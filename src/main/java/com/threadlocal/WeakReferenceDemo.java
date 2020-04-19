/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/13 下午1:16
 */
package com.threadlocal;

import java.lang.ref.WeakReference;

/**
 * 弱引用指向的对象，在GC时就会回收对象占用的内存
 * ThreadLocal.ThreadLocalMap.Entity 继承自 WeakReference
 * @author HanZhonghua
 * @version 1.0
 */
public class WeakReferenceDemo {

    public static void main(String[] args) {
        WeakReference<GCLog> reference = new WeakReference<>(new GCLog());
        System.out.println(reference.get());

        // 执行垃圾回收是守护线程执行的
        System.gc();
        System.out.println(reference.get());
    }
}
