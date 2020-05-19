/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/5/14 下午1:22
 */
package com.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * FutureTask 是一个类，实现了接口 RunnableFuture，RunnableFuture 实现了 Runnable 与 Future，Future 一个接口，主要用来作为接受线程池执行任务的结果
 * Runnable 可以定义一个线程。
 * 因为实现了Runnable 接口，所以FutureTask对象 可以作为一个任务提交给ThreadPoolExecutor 来执行，也可以直接被 Thread 执行，用因为 实现了 Future，所以
 * 可以用来获取任务的执行结果
 *
 * 从下面例子可以看出来：FutureTask 可以用来获取子线程的执行结果
 *
 * 接下来我们使用 FutureTask 来实现一个例子：【烧水泡茶程序】，简单把烧水泡茶分为以下几步：
 *  A：洗水壶（假设耗时1min），烧开水（假设耗时10min），泡茶
 *  B：洗茶壶（假设耗时1min），洗茶杯（假设耗时1min），取茶叶（假设耗时1min）
 *
 *  以上工作都完成之后就可以泡茶了，让我们以程序来模拟最优程序，并发编程的三个核心问题：【分工，协作与互斥】，其中【分工】就是指如何高效的拆解任务，这个例子中，
 *  我们发现 A，B两个任务只有结果是互相影响，就是都需要等对方执行完毕才可以泡茶，但是两个是可以并行执行的，就是可以让一个人做 A，另外一个人做B，等都完成了，在开始泡茶
 *  在程序里，就是让线程 T1 执行任务 A，线程 T2 执行任务 B，然后主线程等待线程 T1，T2执行完毕之后开始泡茶，不过需要注意一点的是，必须等拿茶叶完成后才可以泡茶。
 *  好了，分析完毕，开始写代码吧
 * @author HanZhonghua
 * @version 1.0
 */
public class FutureTaskTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // FutureTask 的基本用法
        // futureTaskDemo();

        // 烧水泡茶 程序
        ThreadPoolExecutor threadPool = ThreadPoolExecutorTest.getThreadPoolExecutor();
        FutureTask<String> f2Task = new FutureTask<>(new T2Task());
        FutureTask<String> f1Task = new FutureTask<>(new T1Task(f2Task));
        threadPool.execute(f1Task);
        threadPool.execute(f2Task);

        System.out.println(f1Task.get());
        threadPool.shutdown();

    }

    static class T1Task implements Callable<String> {

        FutureTask<String> t2Task;
        T1Task(FutureTask<String> t2Task) {
            this.t2Task = t2Task;
        }

        @Override
        public String call() throws Exception {
            System.out.println("T1 洗水壶");
            Thread.sleep(5000);
            System.out.println("T1 烧开水");
            Thread.sleep(3000);
            String result = t2Task.get();
            System.out.println("T1 泡茶");
            Thread.sleep(1000);
            return "上茶：" + result;
        }
    }

    static class T2Task implements Callable<String> {

        @Override
        public String call() throws Exception {
            System.out.println("T2 洗茶壶");
            Thread.sleep(5000);
            System.out.println("T2 洗茶杯");
            Thread.sleep(5000);
            System.out.println("T2 取茶叶");
            Thread.sleep(5000);
            return "龙井";
        }
    }


    /**
     * FutureTask 的基本用法
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void futureTaskDemo() throws InterruptedException, ExecutionException {
        FutureTask<Integer> futureTask = new FutureTask<>(() -> 1 + 2);

        ThreadPoolExecutor threadPool = ThreadPoolExecutorTest.getThreadPoolExecutor();
        threadPool.execute(futureTask);
        System.out.println(futureTask.get());
        threadPool.shutdown();
    }
}
