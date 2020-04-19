/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/13 下午12:49
 */
package com.threadlocal;

import java.lang.ref.SoftReference;

/**
 * 软引用，内存不足时，回收软引用指向的对象占用的内存，可以用来实现缓存
 * @author HanZhonghua
 * @version 1.0
 */
public class SoftReferenceDemo {

    public static void main(String[] args) throws InterruptedException {
        SoftReference<byte[]> softReference = new SoftReference<>(new byte[1024 * 1024 * 10]);

        System.gc();
        System.out.println(softReference.get());

        Thread.sleep(1000);

        System.out.println(softReference.get());

        // 设置了最大堆20M，已经占用了10M，在分配12M时，空间不足，回收软件用指向对象占用的内存
        byte b[] = new byte[1024 * 1024 * 12];

        System.out.println(softReference.get());
    }
}
