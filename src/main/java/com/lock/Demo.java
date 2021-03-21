/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/7/28 下午9:43
 */
package com.lock;

/**
 * @author HanZhonghua
 * @version 1.0
 */
public class Demo {

    public static void main(String[] args) {

        for (int a=0;a<5;a++){
            int b = a;
            if (a>0) {
                --b;
                System.out.println(b+"-"+a);
            }

        }
    }
}
