/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/2/16 下午5:46
 */
package com.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟银行账户存钱和查询余额都要加锁
 * @author HanZhonghua
 * @version 1.0
 */
public class SynchronizedDemo1 {

    private String name;
    private double amount;
    private Map<String, Object> map = new HashMap<>();

    public synchronized void set(String name, double amount) {
        this.name = name;
        try {
            // 模拟存款业务需要耗时4秒
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.amount = amount;
    }

    // 多线程读写同一共享变量，必须保证操作读写的方法加同一把锁
    public synchronized double getAmount(String name) {
        return this.amount;
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedDemo1 s = new SynchronizedDemo1();
        new Thread(()->s.set("zhangsan",200D)).start();
        System.out.println(s.getAmount("zhangsan"));
        Thread.sleep(5000);
        System.out.println(s.getAmount("zhangsan"));
    }

    @Override
    public String toString() {
        return this.name + "-" + this.amount;
    }
}
