/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/5/25 下午1:15
 */
package com.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 学习 CompletionService 之前，先来看个例子，需要将一批商品从不同的电商平台获取其价格写到db进行价格比对，假如有：淘宝，拼多多，小米
 * 分析下可以分为 3 个任务，不同任务分别获取不同平台的商品价格，又因为这 3 个任务没有依赖关系，因此可以并行处理，首先想到的就是使用
 * 线程池。
 *
 * 通过下面的两个例子：发现了使用线程池实现并行，代码有点冗余，还好JUC 为我们提供了API：CompletionService，实现原理是内部维护了一个阻塞队列
 * 当任务结束时将任务结果写入队列中，和下面示例不同的是，CompletionService 是在 submit 时将任务执行结果的Future 写入到队列了.
 * 这个接口的实现类是：ExecutorCompletionService，以下是提供的5个方法
 *
 * Future submit(Callable task);
 * Future submit(Runnable task, V result);
 * Future take() throws InterruptedException; 从任务队列里取出任务执行结果，为空阻塞
 * Future poll(); 从任务队列里取出任务执行结果，为空返回null
 * Future poll(long timeout, TimeUnit unit) throws InterruptedException;
 *
 * 看完用法，来实战下吧， 著名的RPC 框架Dubbo 中，有一种叫做Forking 的集群模式，支持并行的调用多个查询服务，只要有一个成功返回结果，整个服务就可以返回了。
 * 比如需要提供一个地址转坐标的API，为了实现服务的额高可用，同时调用了3 个提供服务的厂商，只要有一个厂商成功返回结果，那个这个地址转坐标的API 就可以返回了
 * 可以容忍另外两个服务异常，但是也有缺点，就是耗费资源过多
 *
 * 总结下：当遇到需要批量运行并行任务时，建议使用 CompletionService ，将Executor 和 BlockingQueue 结合在一起，让批量执行异步任务变得更加简单，还可以
 * 让异步任务执行结果有序化，既哪个任务先执行完，哪个任务执行结果先进队列。利用这个特性，可以轻松实现后续处理的有序性，避免过多等待，像Dubbo 的Forking Cluster
 * 就可用 CompletionService 来实现
 *
 * CompletionService  的实现类 ExecutorCompletionService，可以传入线程池和阻塞队列在构造时候，如果不传队列，默认使用LinkedBlockingQueue 。
 *
 * @author HanZhonghua
 * @version 1.0
 */
public class CompletionServiceTest {

    public static void main(String[] args) throws Exception {
        // 线程池 + Future
        // threadPoolFuture();

        // 线程池 + Future + Queue
        // threadPoolFutureQueue();

        // 使用 CompletionService 批量执行并行任务
        // completionService();

        // 实战：CompletionService 实现Dubbo Forking模式
        CompletionServiceDemo();

    }

    private static void CompletionServiceDemo() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        ExecutorCompletionService<String> ecs = new ExecutorCompletionService<>(service);
        List<Future<String>> futures = new ArrayList<>();

        long start  = System.currentTimeMillis();
        Future<String> f1 = ecs.submit(() -> {
            Thread.sleep(3000);
            return "A获取地址坐标(123,456)";
        });
        futures.add(f1);
        Future<String> f2 = ecs.submit(() -> {
            Thread.sleep(2000);
            return "B获取地址坐标(123,456)";
        });
        futures.add(f2);
        Future<String> f3 = ecs.submit(() -> {
            Thread.sleep(4000);
            return "C获取地址坐标(123,456)";
        });
        futures.add(f3);

        String str;
        for(;;) {
            String s = ecs.take().get();
            if (s != null) {
                str = s;
                // 只要有一个查询服务查询到了，跳出循环
                break;
            }
        }
        long end  = System.currentTimeMillis();
        System.out.println("耗时："+(end-start)+" 获取坐标信息："+str);

        // 取消所有执行任务
        futures.forEach(f -> f.cancel(true));
    }

    /**
     *  线程池 + Future，虽然使用了线程池和异步操作让任务并行起来了，但是在调用 f.get() 时，线程任然会阻塞，这个例子里以供调用了 3次f.get()
     * 就会阻塞3 次，可以看到程序并没有实现真正的并行，严格来说3 个任务还是串行的，怎么解决这个问题呢？
     *  其实解决方案也简单，就是把任务执行结果写到一个队列，然后主线程消费队列
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void threadPoolFuture() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(3);

        List<Object> list = new ArrayList<>();

        long start = System.currentTimeMillis();
        Future<String> f1 = service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("淘宝获取价格");
                Thread.sleep(5000);
                return "淘宝:{item1:10,item2:20,item3:30}";
            }
        });

        Object o = f1.get();
        list.add(o);

        Future<String> f2 = service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("拼多多获取价格");
                Thread.sleep(2000);
                return "pdd:{item1:9.0,item2:19.8,item3:29.8}";
            }
        });

        Object o2 = f2.get();
        list.add(o2);

        Future<String> f3 = service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("小米获取价格");
                Thread.sleep(1000);
                return "小米:{item1:11,item2:21,item3:31}";
            }
        });

        Object o3 = f3.get();
        list.add(o3);
        list.forEach(System.out::println);
        long end = System.currentTimeMillis();

        System.out.println("耗时"+(end-start));
        service.shutdown();
    }

    /**
     * 解决上面方法的问题，增加任务队列，两个线程池，一个线程池执行任务，一个线程池将任务结果异步写入队列
     * 这样就实现了真正的任务并行。
     *
     * 但是在实际中，往往不这样用，因为JUC 已经为我们提供了API，直接使用即可，它就是：CompletionService
     * @throws InterruptedException
     */
    private static void threadPoolFutureQueue() throws InterruptedException {
        ExecutorService service1 = Executors.newFixedThreadPool(3);
        ExecutorService service2 = Executors.newFixedThreadPool(3);

        BlockingQueue<Object> queue = new ArrayBlockingQueue<>(10);

        long start = System.currentTimeMillis();
        Future<String> f1 = service1.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("淘宝获取价格");
                Thread.sleep(5000);
                return "淘宝:{item1:10,item2:20,item3:30}";
            }
        });

        service2.execute(() -> {
            try {
                queue.put(f1.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Future<String> f2 = service1.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("拼多多获取价格");
                Thread.sleep(2000);
                return "pdd:{item1:9.0,item2:19.8,item3:29.8}";
            }
        });

        service2.execute(() -> {
            try {
                queue.put(f2.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Future<String> f3 = service1.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("小米获取价格");
                Thread.sleep(1000);
                return "小米:{item1:11,item2:21,item3:31}";
            }
        });

        service2.execute(() -> {
            try {
                queue.put(f3.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        for(int i=0;i<3;i++) {
            Object take = queue.take();
            System.out.println(take);
        }
        long end = System.currentTimeMillis();

        System.out.println("耗时"+(end-start));
        service1.shutdown();
        service2.shutdown();
    }

    private static void completionService() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        ExecutorCompletionService<String> ecs =  new ExecutorCompletionService<String>(service);

        long start = System.currentTimeMillis();
        ecs.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("淘宝获取价格");
                Thread.sleep(5000);
                return "淘宝:{item1:10,item2:20,item3:30}";
            }
        });


        ecs.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("拼多多获取价格");
                Thread.sleep(2000);
                return "pdd:{item1:9.0,item2:19.8,item3:29.8}";
            }
        });


        ecs.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("小米获取价格");
                Thread.sleep(1000);
                return "小米:{item1:11,item2:21,item3:31}";
            }
        });

        for(int i=0;i<3;i++) {
            Object take = ecs.take().get();
            System.out.println(take);
        }
        long end = System.currentTimeMillis();
        service.shutdown();
        System.out.println("耗时"+(end-start));
    }

}
