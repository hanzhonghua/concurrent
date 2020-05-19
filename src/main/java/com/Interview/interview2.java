/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/28 下午11:09
 */
package com.Interview;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * 两个线程交替打印：A1B2C3D4E5F6...
 * 考的是线程的通信机制，可以使用LockSupport.park() 阻塞当前线程 LockSupport.unpark(Thread t) 唤醒指定线程
 * @author HanZhonghua
 * @version 1.0
 */
public class interview2 {

    private static Thread t1 = null, t2 = null;

    public static void main(String[] args) {
        List<String> l1 = Arrays.asList("A", "B", "C", "D", "E", "F");
        List<Integer> l2 = Arrays.asList(1, 2, 3, 4, 5, 6);


        t1 = new Thread(() -> {
            for (String s : l1) {
                LockSupport.unpark(t2);
                System.out.print(s);
                LockSupport.park();
            }
        });
        t2 = new Thread(() -> {
            for (Integer s : l2) {
                LockSupport.park();
                System.out.print(s);
                LockSupport.unpark(t1);
            }
        });
        t1.start();
        t2.start();

    }
}
