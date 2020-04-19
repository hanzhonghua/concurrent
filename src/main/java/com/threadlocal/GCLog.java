/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/13 下午1:22
 */
package com.threadlocal;

/**
 * @author HanZhonghua
 * @version 1.0
 */
public class GCLog {

    @Override
    protected void finalize() throws Throwable {
        System.out.println("垃圾回收了");
    }
}
