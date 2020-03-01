/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/2/29 下午9:49
 */
package com.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HanZhonghua
 * @version 1.0
 */
public class AtomicIntegerTest {

    private AtomicInteger count = new AtomicInteger(0);

    public void method() {
        count.incrementAndGet();
    }

    public static void main(String[] args) {

    }
}
