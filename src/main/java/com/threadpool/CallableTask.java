/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/5/2 下午10:48
 */
package com.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author HanZhonghua
 * @version 1.0
 */
public class CallableTask implements Callable {

    private int a;

    CallableTask(){}

    CallableTask(int a) {
        this.a = a;
    }

    @Override
    public Object call() throws Exception {
        System.out.println("线程：" + Thread.currentThread().getName() + " 执行任务：" + "Task-" + a);
        Thread.sleep(ThreadLocalRandom.current().nextInt(10000));
        return a;
    }
}
