/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/3/7 下午11:32
 */
package com.deadlock;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述出现死锁的情况以及如何排查死锁与预防死锁
 *
 *      上个代码【AccountDeadLock】示例中主要展示了死锁是如何产生的：线程T1占用资源A等待释放资源B，线程T2占用资源B等待释放资源A，就这样一直互相等待下去，就产生了死锁。会影响本次操作失败，
 * 当程序发现死锁时，往往需要重启服务器，因此，避免死锁的最好办法就是避免死锁。有人总结除了只要同时满足以下条件时才会发生死锁：
 * 1.【互斥】：共享资源X，Y只能被一个线程占用
 * 2.【占有且等待】：线程T1已经占有资源X，在等待资源Y时候，不释放资源X
 * 3.【不可抢占】：其它线程不可强制占用线程T1已经占有的资源X
 * 4.【循环等待】：线程T1等待线程T2占有的资源，线程T2等待线程T1占有的资源 ，这就是循环等待了
 *
 * 只要我们破坏以上任意条件就可以破坏死锁了。
 * 首先看第一点【互斥】，这个是保证多线程下原子性的必要条件，不可以破坏；
 * 然后看第二点【占有且等待】，让线程一次性的申请两个资源，这样就不存在等待了
 * 第三点【不可抢占】，如果线程T1占有T2，却申请不到资源Y，让线程T1释放资源X
 * 第四点【循环等待】，可以顺序申请资源，就是指资源是有线性顺序的，可以先申请资源号小的，然后再申请资源号大的
 * @author HanZhonghua
 * @version 1.0
 */
public class AccountSloveDeadLock {

    public static void main(String[] args) throws InterruptedException {
        // 测试解决互斥锁 - 破坏占有且等待条件
        // havingAndWaitTest();
        /**
         * 测试解决互斥锁 - 破坏不可抢占条件，线程如果占有资源A，去申请另外的资源，一直申请不到，线程释放占有资源
         * 但是synchronized是做不到的，因为synchronized申请资源如果申请不到，线程会进入阻塞状态，进入阻塞状态
         * 什么也做不了。这也是synchronized缺点之一，不灵活，不能够尝试加锁加锁失败释放资源，JUC.ReentrantLock就是解决这个问题
         * 的，到项目【lock】包下讲解ReentrantLock时候再讲解
         */

        // 破坏循环等待条件 - 顺序申请资源，就是保住加锁的顺序，
        transferOrderTest();
    }


//---------------------------------------------------------------------------------------------------------------------------------

    /**
     * 破坏占有且等待条件测试
     * @throws InterruptedException
     */
    private static void havingAndWaitTest() throws InterruptedException {
        HavingAndWait tom = new HavingAndWait("tom" ,200);
        HavingAndWait rose = new HavingAndWait("rose", 200);
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

    /**
     * 破坏循环等待条件测试
     * @throws InterruptedException
     */
    private static void transferOrderTest() throws InterruptedException {
        AccountOrder tom = new AccountOrder(1, "tom", 200);
        AccountOrder rose = new AccountOrder(2, "rose", 200);
        System.out.println("转账前余额：" + tom.name + ":" + tom.balance + ";" + rose.name + ":" + rose.balance);
        Thread t1 = new Thread(() -> tom.transfer(tom, rose, 50), "t1");
        Thread t2 = new Thread(() -> rose.transfer(rose, tom, 100), "t2");
        t1.start();
        t2.start();
        // 主线程等待转账线程执行完毕，正如去银行转账等待操作人员操作完成
        t1.join();
        t2.join();
        System.out.println("转账后余额：" + tom.name + ":" + tom.balance + ";" + rose.name + ":" + rose.balance);
    }
}

//---------------------------------------------------------------------------------------------------------------------------------

/**
 * 破坏循环等待 - 保住加锁顺序，也是以转账为例，每个用户都有一个唯一的id，按照id降序排序加锁，便可解决问题
 */
class AccountOrder {
    int id;
    String name;
    int balance;
    AccountOrder(int id, String name, int balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    void transfer(AccountOrder from, AccountOrder to, int balance) {
        AccountOrder left = from;
        AccountOrder right = to;
        if (from.id > to.id) {
            left = to;
            right = from;
        }
        // 锁定小id账户
        synchronized (left) {
            System.out.println("获得转出账户账本,操作员 ："+Thread.currentThread().getName()+"; 账户："+from.name);
            try {
                // 线程休眠一下，等待2个线程都获得锁
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 锁定大id账户
            synchronized (right) {
                System.out.println("获得转入账户账本,操作员 ："+Thread.currentThread().getName()+"; 账户："+to.name);
                if (from.balance >= balance) {
                    // 查询数据库业务逻辑，睡眠模拟
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    from.balance -= balance;
                    to.balance += balance;
                    System.out.println("操作员: "+Thread.currentThread().getName() +" 执行转账 "+from.name +" 转给 " + to.name + balance + "元");
                }
            }
        }

    }
}

//---------------------------------------------------------------------------------------------------------------------------------

/**
 * 解决占有且等待
 *      还是拿转账例子来说，需要同时拿到转出账本和转入账本，才可以执行转账。这样一来，就不能让业务员来拿账本了，应该加一个管理员，当业务员来拿账本时，
 * 管理员先看看转出与转入账本是否都存在，如果都存在，才让业务员拿走账本。这样业务员要么同时拿走两个账本，要么等待拿，就不存在占有且等待了
 *      在Java程序实现的时候，作为管理员，我们需要设计一个类【HavingAndWait】，这个类是单例的，并且有两个重要的方法，同时申请资源apply和同时释放资源release，在
 * 转账操作类里需要引入【HavingAndWait】，并且是单例的
 */
class HavingAndWait {

    String name;
    int balance;
    HavingAndWait(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }
    // 账本管理员，必须是单例
    private AccountAdmin accountAdmin = AccountAdmin.getInstance();

    public void transfer(HavingAndWait account, HavingAndWait target, int amount) {

        while (!accountAdmin.apply(account, target)) {
            try {
                System.out.println("没有同时拿到两个账本，休息下继续拿，操作员："+Thread.currentThread().getName());
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            // 锁定转出账户
            synchronized (account) { // 1
                System.out.println("获得转出账户账本,操作员 ："+Thread.currentThread().getName()+"; 账户："+account.name);
                try {
                    // 线程休眠一下，等待2个线程都获得锁
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 锁定转入账户
                synchronized (target) { // 2
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
class AccountAdmin {

    private AccountAdmin(){}

    private List<Object> list = new ArrayList<>();
    // 申请
    synchronized boolean apply(Object from, Object to) {
        if (list.contains(from) || list.contains(to)) {
            return false;
        } else {
            list.add(from);
            list.add(to);
            return true;
        }
    }

    // 归还
    synchronized void release(Object from, Object to) {
        list.remove(from);
        list.remove(to);
    }

    static AccountAdmin getInstance() {
        return Singleton.instance;
    }
    private static class Singleton {
        private static AccountAdmin instance = new AccountAdmin();
    }
}
