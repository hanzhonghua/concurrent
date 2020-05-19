/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/5/1 上午10:21
 */
package com.threadpool;

import javax.xml.transform.Result;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * juc 提供了很多线程池工具类，可以通过Executors 的方法创建各种各样的线程池，但是非常不建议使用自带的，因为他的自带线程池自带的任务队列是
 * 无界的，如果任务量巨大的时候容易造成内存溢出，所有这些工具不做详细研究
 * FixedThreadPool SingleThreadExecutor 任务队列长度是Integer.MAX_VALUE 容易造成内存溢出，CachedThreadPool 的最大线程数是Integer.MAX_VALUE
 * 会导致大量创建线程，导致内存溢出
 *
 * 阿里开发规范上：
 *  1.需要自己实现 【ThreadPoolExecutor】 来定义线程池，然后创建线程要定义线程名，方法在出现问题时便于追溯
 *  2.必须使用线程池来创建线程，在程序任意地方不能显示创建，线程池的好处之一就是减少创建和销毁所耗费的时间及系统资源消耗，解决资源不足问题
 *    如果显示创建可能造成系统大量的线程导致消耗内存或者线程"过度切换"的问题
 *
 *  ThreadPoolExecutor(int corePoolSize,  // 核心线程数，空闲下来这些线程也不会被回收，当然可以配置allowCoreThreadTimeOut(boolean) 设置是否对核心线程有效
 *                     int maximumPoolSize, // 最大线程数，当核心线程数满了，工作队列满了，就会创建新的线程，直到达到最大线程数，如果达到最大线程数还有任务进来，就会使用拒绝策略
 *                     long keepAliveTime,  // 线程空闲时存活时间（非核心线程）
 *                     TimeUnit unit, // 存活时间单位
 *                     BlockingQueue<Runnable> workQueue, // 任务队列
 *                     ThreadFactory threadFactory,  // 线程工厂
 *                     RejectedExecutionHandler handler) // 拒绝策略
 *
 * 举个显示中的例子，比如工厂有10个员工做业务，排队的业务较多，当多于10个待业务时，其它在来的任务就要排队了，当排队的任务较多，并且排队速度远大于处理速度，比如超过 10 个，
 * 就要在招员工了，招了 5 个临时员工，再来任务就要交给这 5 个员工处理了，当这 15 个人还处理不及时，就要选择抛弃一些任务了。当这个 15 员工空闲时，而待处理的任务又非常少时
 * 就要选择辞掉 这 5 个临时工了
 * 以上 10 个核心员就表示 "核心线程"了，15 个员工就表示最大线程数了，排队就是任务队列，抛弃就是对应着拒绝策略了
 *
 * 以上7个形参，任务队列以及线程工厂和拒绝策略都是建议自己实现接口，定制自己所需的逻辑。有一点需要说明，刚开始建好的线程池是没有活动线程的，有任务进来在创建
 *
 * 常用的BlockingQueue 有：
 * ArrayBlockingQueue 基于数组的FIFO队列，构造时必须显示传入长度
 * LinkedBlockingQueue 基于链表的FIFO队列，不指定长度默认Integer.MAX_VALUE
 * synchronousQueue 这个队列不保存提交的任务，而是直接新建一个线程执行任务
 *
 * 默认拒绝策略有：
 * ThreadPoolExecutor.AbortPolicy  丢弃任务并抛出：RejectedExecutionException
 * ThreadPoolExecutor.DiscardPolicy 丢弃任务并不会抛出异常
 * ThreadPoolExecutor.DiscardOldestPolicy 丢弃队列最前的任务，然后重新尝试执行任务
 * ThreadPoolExecutor.CallerRunsPolicy 由调用线程处理该任务
 * 还可以实现接口，自定义拒绝策略，比如异步写到MQ里，异步消费处理
 *
 * 线程池执行任务/启动 有两个方法 execute() 和 submit() 前者需要需要实力类实现Runnable接口，后者支持实现了Callable的实例类，这个接口用途和Runnable一样，
 * 只是多了返回值和异常处理。前者没有返回值，后者用Future 作为返回，可以接收线程池执行任务的返回信息，底层也是使用的execute()
 * 线程池终止有两个方法 shutdown() shutdownNow() 前者不会立即终止线程，只是不再接口新任务，还会等待队列里或者正在执行的任务执行完毕，而后者是直接中断线程
 * 可能会存在任务丢失等异常情况，不要使用后者
 *
 * @author HanZhonghua
 * @version 1.0
 */
public class ThreadPoolExecutorTest {

    public static ThreadPoolExecutor getThreadPoolExecutor () {

        // 任务队列
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);
        return new ThreadPoolExecutor(5, 10, 60, TimeUnit.MILLISECONDS, workQueue,
                new DefaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public static void main(String[] args) {

        ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();

        // execute Runnable
        testRunnable(threadPoolExecutor);

        // submit Callable
        // testCallable(threadPoolExecutor);

    }

    private static void testCallable(ThreadPoolExecutor threadPoolExecutor) {

        try {
            List<Future> futureList = new ArrayList<>();
            for (int i = 1; i< 10; i++) {
                Future<?> submit = threadPoolExecutor.submit(new CallableTask(i));
                futureList.add(submit);
            }
            System.out.println("thread-pool is shutdown ? " + threadPoolExecutor.isShutdown());
            System.out.println("thread-pool is terminated ? " + threadPoolExecutor.isTerminated());

            for (Future f : futureList) {
                try {
                    System.out.println(f.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } finally {
            threadPoolExecutor.shutdown();
        }
        System.out.println("thread-pool is shutdown ? " + threadPoolExecutor.isShutdown());
        System.out.println("thread-pool is terminated ? " + threadPoolExecutor.isTerminated());
    }

    private static void testRunnable(ThreadPoolExecutor threadPoolExecutor) {
        try {
            int a = 0;
            for (int i = 1; i< 10; i++) {
                a = i;
                try {
                    threadPoolExecutor.execute(new RunnableTask(a));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("thread-pool is shutdown ? " + threadPoolExecutor.isShutdown());
            System.out.println("thread-pool is terminated ? " + threadPoolExecutor.isTerminated());

        } finally {
            threadPoolExecutor.shutdown();
        }
        System.out.println("thread-pool is shutdown ? " + threadPoolExecutor.isShutdown());
        System.out.println("thread-pool is terminated ? " + threadPoolExecutor.isTerminated());
    }


}
