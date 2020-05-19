/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/5/16 下午5:24
 */
package com.threadpool;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;

/**
 * 异步编程
 * 异步化，是提升程序性能一个重要手段，也是利用多线程优化性能一个重要基础。前面已经学习了如何使用多线程以及如何使用线程池来实现异步编程，
 * 在这里学习加CompletableFuture，【jdk1.8】出现的异步编程工具，
 *
 * 先来看个例子，还是以烧水泡茶为例：这里拆分 3 个任务，【任务1】 负责洗水壶，烧开水，【任务2】 负责洗茶壶，洗茶杯，拿茶叶，【任务3】 负责泡茶；
 * 基于使用线程池实现的烧水泡茶程序来说，使用CompletableFuture 的优点：
 *  1.实现的更加简洁，不需要自己维护线程，为任务分配线程也无需关注，代码几乎都是和业务逻辑相关的
 *  2.语义更加清晰，比如：f3 = f1.thenCombine(f2, ()->{}) 就表示f3 等待f1 和f2 执行完毕再执行
 *
 * 了解了CompletableFuture 实现异步编程的优点之后，再来研究下它的一些基本用法
 * 创建CompletableFuture 主要有4 个静态方法：
 * CompletableFuture<Void> runAsync(Runnable runnable)  无返回值
 * static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) 有返回值
 * static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
 * static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
 *
 * 前两个方法默认使用ForkJoinPool 线程池，创建的线程数是CPU 核数，如果CompletableFuture 都使用默认线程池，如果有一个线程执行任务很慢，就会导致
 * 线程池其它线程一直阻塞IO，所有要自己创建线程池。
 *
 * 创建好CompletableFuture 之后会自动执行runnable.run() 或supplier.get() 方法。对于一个异步操作，要关注来两个问题：什么时候结束以及返回值，因为
 * CompletableFuture 实现了Future 接口，所有这两个问题都可以通过Future 解决
 * 还实现了 CompletionStage 接口，这个接口也是jdk1.8提供的，要了解 CompletionStage首先要理解"串行"，"并行"，与"汇聚"关系，还以烧水泡茶为例，
 * 在【任务1】里，洗水壶，烧开水就属于"串行"，洗水壶和【任务2】洗茶壶就是"并行"关系，而"泡茶"需要【任务1，2】执行完成才可以，属于"汇聚"关系，
 * f3 = f1.thenCombine(f2, ()->{}) 描述的就是"汇聚"关系，烧水泡茶的"汇聚"是 AND 关系，就是所有的任务执行完毕之后才可以泡茶，既然有 AND ，
 * 就会有 OR，所谓 OR 就是指依赖的任务只要有一个完成就行
 *
 * 异步编程的另外一个问题就是如何处理异常了，
 * @author HanZhonghua
 * @version 1.0
 */
public class CompletableFutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 任务1 洗水壶，烧开水
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            System.out.println("T1 洗水壶");
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("T1 烧开水");
            try {
                sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 任务2 洗茶壶，洗茶杯，拿茶叶
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {

            System.out.println("T2 洗茶壶");
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("T2 洗茶杯");
            try {
                sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("T3 拿茶叶");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "龙井";
        });

        // 任务3 等待1，2任务执行完毕，然后泡茶
        CompletableFuture<String> f3 = f1.thenCombine(f2, (s1, tf) -> {
            System.out.println("T1 拿到茶叶：" + tf);
            System.out.println("T1 开水泡茶");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "上茶：" + tf;
        });

        System.out.println(f3.get());
    }
}
