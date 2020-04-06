package com.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 信号量是实现线程通信的一个工具类,主要用来限流，这个限流只是指的限制的并发量
 * 信号量模型比较简单，由一个计数器，一个等待队列，三个方法构成，计数器维护同时通过的线程数量，等待队列用了存放等待的线程
 * 三个方法分别是init，up，down，init就是初始化计数器，up就是计数器+1，down就是计数器-1，对应的分别是acquire和release方法
 * actuire计数器-1，如果此时计数器<0，则当前线程阻塞；releasse计数器计数器+1，如果此时计数器<=0，当前线程阻塞，否则执行
 * 当一个线程执行完毕时调用计数器+1，会唤醒等待队列中线程，只会唤醒一个，并且唤醒的线程也会立即执行。
 * 和锁的区别主要时java中Lock可以唤醒多个线程去争夺锁，这个机制是用Condition实现的，而信号量中是没有Condition概念的，
 * 在信号量等待队列中一旦阻塞线程被唤醒，就会立即执行，而不会去判断临界区条件，所以才会每次唤醒的只是一个等待线程
 */
public class SemaphoreDemo {

    public static void main(String[] args) {
        final Semaphore semaphore = new Semaphore(5);
        ExecutorService service = Executors.newFixedThreadPool(50);

        for (;;) {
            service.execute(new Runnable() {
                public void run() {
                    try {
                        semaphore.acquire();
                        Thread.sleep(1000);
                        System.out.println("当前线程："+Thread.currentThread().getName()+"--等待队列长度" +semaphore.getQueueLength());
                        System.out.println("--"+semaphore.availablePermits());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finally {
                        semaphore.release();
                    }
                }
            });
        }
    }
}
