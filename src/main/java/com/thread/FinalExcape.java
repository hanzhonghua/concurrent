/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/3/1 下午3:37
 */
package com.thread;

/**
 * final修饰的变量时线程安全的，可以随意优化，但是要注意一定要避免【逸出】
 * 下面的例子说明下什么是【逸出】
 * @author HanZhonghua
 * @version 1.0
 */
public class FinalExcape {

    private final int a;
    public FinalExcape fe;

    FinalExcape() {
        a = 3;
        // 构造方法中将this赋值给了全局变量fe，由于存在指令重排，此时通过fe读到的a可能是0
        fe = this;
    }

}
