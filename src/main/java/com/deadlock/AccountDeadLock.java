/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/3/5 下午11:15
 */
package com.deadlock;

/**
 * 描述为何需要锁以及锁细化以及出现死锁
 * @author HanZhonghua
 * @version 1.0
 */
public class AccountDeadLock {

    private int balance;

    public AccountDeadLock(int balance) {
        this.balance = balance;
    }

    /**
     * 模拟转账，如果当前"小李子"余额大于要转钱，就减掉要转金额，目标账户+金额
     * 这样写代码看着没有问题，但是在并发环境非常容易出问题，比如线程A执行到代码1
     * 要转账200，发现正好够转，于是就开始执行，还没有执行完还没有执行到代码2的时候
     * 线程B来执行转账了，发现也满足条件，也开始扣减，就会导致用户账户被转成负数。怎么解决呢？
     * 当然是加锁了
     *
     *      第一次加锁转账方法上加synchronized，锁的对象是当前对象this，看着是没有问题了。但是在实际中还会存在问题，举个例子
     * 假如现在有三个用户A，B，C，账户余额都是200，先A转B100，B转C100，我们期望的最终账户余额是A100，B200，C300，但是
     * 在并发场景会出现与理想不一致的情况。分析一下，假如现在有两个线程，T1执行，A转B，T2执行B转C，T1和T2在两个线程上运行
     * 那么这两个线程会互斥么？显然不会，因为T1锁定的是账户A的实例（A.this），而T2锁定的是B的实例（B.this），所有就起不到
     * 互斥作用，这两个线程也就可以同时进入临界区，可能导致的B的账户余额是300（T1后于T2，T2的写操作被T1写操作覆盖），也有
     * 可能是100（T2覆盖T1写操作），那么怎么解决这个问题的？问题的根源是如果让T1和T2互斥，可以使用Account.class作为锁对象
     * 因为Account.class是所有的Account对象共享的这样一来，并发代码的问题就解决了。
     *      通过这个例子说明了在使用锁时候要主要锁和锁保护资源的关系，要合理的运用锁正确的保护资源，不可能用A的锁保护B的资源。如果
     * A，B资源没有关系，就可以各自使用各自的锁，如果AB存在关系，正如转账场景 ，那么就要使用一个粒度较大的锁。其实就是保证
     * 操作的【原子性】，原子性的本质其实不是不可分割，不可分割只是外在表现，实际是【操作的中间状态对外不可见】。比如32位机器
     * 写long有中间状态（只写了64位中的32位），银行转账也有中间状态（A转B，A账户扣减了，B账户还没有来的及变化），【【【其实只要保证
     * 可中间状态对外不可见，也就保证了原子性】】】
     *
     *      但是还有个问题，就是所有的用户转账都串行化了，想想支付宝每天那么多的转账记录，假入转账操作性能非常高，单机TPS到100，
     * 这样1天才支持24*60*60*100不到1千万的转账次数，显然不可以的，那么有没有解决方案呢？当然是有的就是锁细粒度化，具体如何
     * 细粒度化呢？
      */
    /*public *//*synchronized*//* void transfer(Account account, Account target, int amount) {
        synchronized (Account.class) {
            if (account.balance >= amount) {  // 1
                // 查询数据库业务逻辑，睡眠模拟
                try {
                    Thread.sleep(new Random().nextInt(100));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                account.balance -= amount;   // 2
                target.balance += amount;
            }
        }
    }*/

    /**
     * 锁细粒度化优化，我们在现实中找例子。现实中，转账是支持高并发的，银行有很多营业点，每个营业点有很多窗口，都是可以
     * 同时转账的，只要按照现实生活的做，这种问题就迎刃而解了。
     * 在古代钱庄：每个人的账户都对应一个账本，所有用户的账本都放在文件夹里，当发生转账时，必须拿到A账本和B账本才可以，
     * 先看A账本余额是否充足，然后扣减，接口在B账本上加钱，在操作人员拿账本的时候可能会有以下3中情况
     *
     * 1.AB账本都在，同时拿走两个账目，转账成功，归还账本
     * 2.A账本在，B账本不在，等待其他操作员还B账本
     * 3.AB都不在，同时等待两个账本归还
     *      如下代码示例，先尝试锁定账户account，成功了然后尝试锁定target，成功了转账，这样就把锁粒度细化到了当个账户级别。
     * A账户转B账户，C账户转D账户就可以并行执行了。
     *      这种优化方案叫做锁细化，这样看来，已经很完美了，但是锁细化是有代价的，就是死锁。举个例子：
     * 假入业务员T1，T2，T1要账户A转B100，T2要B转A100，业务员T1拿着A的账本，等待归还B账本，而业务员T2拿着B账本，等待
     * 归还A账本，就这样，互相一直等待对方归还账本，就是所谓的死锁
     *
     * 如下代码示例：假如两个线程T1与T2，两个账户A与B，线程T1执行A转B，线程T2执行B转A，在执行到下列代码【1】位置时，线程T1
     * 获取到了A账户的锁，线程T2获取到了B账户的锁，当执行到代码【2】时，线程T1想要获取B账户锁，线程T2要获取A账户锁，这就形成了
     * 【死锁】；具体如何解决呢，看【AccountSloveDeadLock】
      */
    public void transfer(AccountDeadLock account, AccountDeadLock target, int amount) {
        // 锁定转出账户
        synchronized (account) { // 1
            System.out.println("获得转出账户锁："+Thread.currentThread().getName());
            try {
                // 线程休眠一下，等待2个线程都获得锁
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 锁定转入账户
            synchronized (target) { // 2
                System.out.println("获得转入账户锁："+Thread.currentThread().getName());
                if (account.balance >= amount) {
                    // 查询数据库业务逻辑，睡眠模拟
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    account.balance -= amount;
                    target.balance += amount;
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AccountDeadLock tom = new AccountDeadLock(200);
        AccountDeadLock jerry = new AccountDeadLock(200);
        Thread t1 = new Thread(() -> {
            tom.transfer(tom, jerry, 100);
        },"t1");
        Thread t2 = new Thread(() -> {
            jerry.transfer(jerry, tom, 50);
        },"t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(tom.balance+"-"+jerry.balance);
    }
}
