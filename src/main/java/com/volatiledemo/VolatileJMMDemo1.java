/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/3/1 下午1:38
 */
package com.volatiledemo;

/**
 * 如下代码，读取时，x一定是40，主要是根据Happens-Before的几项规则
 * 1.同一个线程内，前面的操作Happens-Before后续的操作
 * 2.一个volatile变量修饰的变量，写Happens-Before对这个变量的读
 * 3.A Happens-Before B，B Happens-Before C，则A Happens-Before C
 * 首先根据第一条规则，write方法中，x=40 Happens-Before b = true，然后根据
 * 第二条规则，如果有另外一个线程B执行reader方法，则b = true Happens-Before 读b
 * 根据依赖传递性，则x = 40 Happens-Before 读b，所以此时读取的x一定是40；
 * 注意这个语义是在java1.5之后才有的
 *
 * int a = 1; //1
 * int b = 2; //2
 * volatile c = 3; //3
 * int d = 4; //4
 * int e = 5; //5
 * 如上代码，1和2一定发生在3之前，这是volatile内存语义的约定，而又由于指令重排序，1和2发生顺序不一定（指令重排序：在不影响
 * 程序执行结果的情况下，允许对操作进行重排序，当然这个不影响是在一个线程内部，而1和2顺序变化对结果不影响，所有可能存在重排）
 * 同理4，5一定在4之后，而4和5的执行顺序也不确定
 * @author HanZhonghua
 * @version 1.0
 */
public class VolatileJMMDemo1 {

    int x = 0;
    volatile boolean b = false;

    void write() {
        x = 40;
        b = true;
    }

    void reader() {
        if (b) {
            System.out.println(x);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        VolatileJMMDemo1 d = new VolatileJMMDemo1();
        for (;;) {
            new Thread(d::write).start();
            new Thread(d::reader).start();
        }
    }
}
