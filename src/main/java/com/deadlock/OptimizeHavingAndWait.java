/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/3/8 下午10:32
 */
package com.deadlock;

import java.math.BigDecimal;
import java.util.*;

/**
 * 优化占有等待破解方案
 *     【AccountSloveDeadLock.java】中【HavingAndWait】介绍了使用管理员解决占有且等待死锁条件
 * 用到了【while(*.apply())】来轮询获取执行权，如果这个操作占用时间不长，并发量不大，循环个几次还是没有问题的，如果
 * 操作时候占有很长，并发量大的时候，这种方案就不太合适了，因为这种场景，可能循环几千次才能获得锁，而循环的时候是占有
 * CPU资源的，这样就造成了CPU资源的浪费。那么可不可以这么做：当一个线程去获取锁时候，发现资源已经被占用了，就等待着
 * 而占有资源的线程在执行完毕之后释放资源并通知等待中的线程，可以来尝试占用资源了，这样一来，CPU资源也就可以节约出来
 * 在代码实现层面，Java中提供了wait notify notifyAll（等待通知机制）就是用到这种场景的
 * @author HanZhonghua
 * @version 1.0
 */
public class OptimizeHavingAndWait {

    public static void main(String[] args) throws InterruptedException {
        OptimizeHavingAndWait tom = new OptimizeHavingAndWait("tom" ,200);
        OptimizeHavingAndWait rose = new OptimizeHavingAndWait("rose", 200);
        System.out.println("转账前余额：" +tom.name+":"+tom.balance+";"+rose.name+":"+rose.balance);
        Thread t1 = new Thread(()->tom.transfer(tom, rose, 50), "t1");
        Thread t2 = new Thread(()->rose.transfer(rose, tom, 100), "t2");
        t1.start();
        t2.start();
        // 主线程等待转账线程执行完毕，正如去银行转账等待操作人员操作完成
        t1.join();
        t2.join();
        System.out.println("转账后余额："+tom.name+":"+tom.balance+";"+rose.name+":"+rose.balance);

    }

    String name;
    int balance;
    OptimizeHavingAndWait(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }
    // 账本管理员，必须是单例
    private AccountAdmin2 accountAdmin = AccountAdmin2.getInstance();

    public void transfer(OptimizeHavingAndWait account, OptimizeHavingAndWait target, int amount) {

        try {
            // 管理员申请资源
            accountAdmin.apply(account, target);
            // 锁定转出账户
            synchronized (account) {
                System.out.println("获得转出账户账本,操作员 ："+Thread.currentThread().getName()+"; 账户："+account.name);
                try {
                    // 线程休眠一下，等待2个线程都获得锁
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 锁定转入账户
                synchronized (target) {
                    System.out.println("获得转入账户账本,操作员 ："+Thread.currentThread().getName()+"; 账户："+target.name);
                    if (account.balance >= amount) {
                        // 查询数据库业务逻辑，睡眠模拟
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        account.balance -= amount;
                        target.balance += amount;
                        System.out.println("操作员: "+Thread.currentThread().getName() +" 执行转账 "+account.name +" 转给 " + account.name + amount + "元");
                    }
                }
            }
        } finally {
            accountAdmin.release(account, target);
        }
    }
}


// 账本管理员
class AccountAdmin2 {

    private AccountAdmin2(){}

    private List<Object> list = new ArrayList<>();
    // 申请
    synchronized void apply(Object from, Object to) {
        System.out.println(Thread.currentThread().getName() + " 申请操作");
        // 为什么使用轮询，因为如果使用释放锁的实际不确定
        while (list.contains(from) || list.contains(to)) {
            try {
                System.out.println(Thread.currentThread().getName() + " 休眠");
                // 当前对象对应的等待队列，如果线程执行到这里时候，不满足条件，会进入条件变量对应的等待队列
                // 注意和synchronized上的等待队列还不太一致，synchronized上的等待队列是多个线程竞争一把锁时
                // 除了获得锁的线程都会进入对应的synchronized等待队列【锁队列】，也可以认为是入口等待队列。当获得锁的线程
                // 继续执行发现不满足条件进入wait的等待队列【条件队列】，会释放锁
                // 调用notify后，会将【条件队列】的线程移到【锁队列】，可以参数争取锁
                getInstance().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " 获得操作权限");
        list.add(from);
        list.add(to);
    }

    // 归还
    synchronized void release(Object from, Object to) {
        list.remove(from);
        list.remove(to);
        // notify会唤醒当前对象对应的等待队列里随机的一个线程，而notifyAll唤醒的是当前对象对应等待队列里所有的线程
        // 看似notify比较好，但是notify可能会导致有的线程一直通知不到，所以如果不是深思熟虑，或者确保只有一个线程等待
        // 那么还是notifyAll比较好一点
        // todo synchronized具体实现是管程，这个在【lock】包下会详细介绍
        getInstance().notifyAll();
        System.out.println(Thread.currentThread().getName() + " 执行完毕，释放资源，唤醒其它线程");
    }

    static AccountAdmin2 getInstance() {
        return Singleton.instance;
    }
    private static class Singleton {
        private static AccountAdmin2 instance = new AccountAdmin2();
    }
}
