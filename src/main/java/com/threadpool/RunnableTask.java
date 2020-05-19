/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/5/2 下午10:46
 */
package com.threadpool;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author HanZhonghua
 * @version 1.0
 */
public class RunnableTask implements Runnable {

    private int i;

    RunnableTask(int i) {
        this.i = i;
    }

    @Override
    public void run() {
        System.out.println("线程：" + Thread.currentThread().getName() + " 执行任务：" + "Task-" + i);
        try {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(10000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
