/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/5/22 下午12:33
 */
package com.threadpool;

import org.apache.commons.lang3.ArrayUtils;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 在Fork/Join 并行计算框架中，Fork 就是对应的任务分解，而Join 就是对应的任务结果合并。Fork/Join 计算框架包括两部分，一部分是分治任务的线程池【ForkJoinPool】，另外一部分
 * 是分治的任务【ForkJoinTask】，两者关系可以理解为 ThreadPoolExecutor 和 Runnable 的关系，都可以提交任务到线程池，不过分治任务有自己独立的【ForkJoinTask】
 *
 * 【ForkJoinTask】是一个抽象类，有许多方法，最重要的还是有两个：fork()，join()；fork()会异步的执行一个子任务，join()阻塞当前线程等待任务执行结果，
 * ForkJoinTask 有两个子类——RecursiveAction 和 RecursiveTask，它们都是用递归方式来处理分治任务的，都定义了抽象方法：compute()，RecursiveAction 没有返回值，
 * RecursiveTask 有返回值，这两个子类也是抽象的，实现时，可以继承然后扩展
 *
 * 之前学习的ThreadPoolExecutor ，实际上使用的生产者消费者模式，内部有一个任务队列，是生产者和消费者的通信媒介，可以使用多线程，多线程内部共享这个任务队列。
 * 而ForkJoinPool 本质也是一个生产者消费者的实现，内部有多个任务队列，当通过ForkJoinPool 的invoke() 或者submit() 提交任务的时候，会根据一定的路由规则把任务提交到
 * 一个任务队列中。如果任务在执行的过程中创建了子任务，那么子任务会提交到工作线程对应的任务队列中。
 * ForkJoinPool 中还有一种【任务窃取】的机制，如果工作线程空闲了，可以【窃取】其它工作队列中的任务，其中的任务队列是采用的【双端队列】，工作线程正常获取的任务和【窃取的任务】
 * 分别是从任务不同的端消费，这样可以避免不必要的数据竞争.
 *
 * Java8 中的stream API的并行流就是以ForkJoinPool 为基础的，需要注意的是，默认所有的并行流都共享一个ForkJoinPool，这个ForkJoinPool 默认线程数是CPU 的核数。
 * 如果所有的并行流计算都是CPU 密集型计算的话，完全没有问题，但是如果存在 IO 密集型计算的话，那么很有可能因为一个慢 IO 阻塞系统性能，因为在使用stream 流时，需要
 * 注意场景要避免IO 密集型计算，还有用不同的ForkJoinPool 执行不同类型的计算任务
 * @author HanZhonghua
 * @version 1.0
 */
public class ForkJoinPoolTest {

    public static void main(String[] args) {

        /*ForkJoinPool fjp = new ForkJoinPool(5);
        Fibonacci f = new Fibonacci(30);

        Integer invoke = fjp.invoke(f);
        System.out.println(invoke);*/

        try {
            System.out.println(1);
            if (true) {
                throw new RuntimeException("1");
            }
            System.out.println(2);
        }finally {
            System.out.println(3);
        }
    }

    static class Fibonacci extends RecursiveTask<Integer> {

        private int n;
        Fibonacci(int n) {
            this.n = n;
        }

        @Override
        protected Integer compute() {
            if (n<=1) {
                return n;
            }
            Fibonacci f1 = new Fibonacci(n - 1);
            // 创建子任务
            f1.fork();
            Fibonacci f2 = new Fibonacci(n - 2);
            return f2.compute() + f1.join();
        }
    }
}
