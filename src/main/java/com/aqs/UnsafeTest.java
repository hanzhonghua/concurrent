/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/8 下午11:18
 */
package com.aqs;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Unsafe.compareAndSwapInt 是通过反射根据字段偏移量修改对象的，int偏移量4，long偏移量4，String偏移量4
 * Unsafe需要通过反射获取
 * @author HanZhonghua
 * @version 1.0
 */
public class UnsafeTest {

    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Class clazz = Target.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            // 获取属性偏移量，通过这个偏移量给属性设置值
            System.out.println(f.getName() + " 偏移量 :" + unsafe.objectFieldOffset(f));
        }

        Target target = new Target();
        Field intParam = clazz.getDeclaredField("intParam");
        int a = (int) intParam.get(target);
        System.out.println("原始值：" +a);

        // intParam的偏移量是12，原始值是3，改为10
        System.out.println(unsafe.compareAndSwapInt(target, 12, 3, 10));
        int b = (int) intParam.get(target);
        System.out.println("改之后的值：" +b);

        // 此时这个值已经改过了，返回false
        System.out.println(unsafe.compareAndSwapInt(target, 12, 3, 10));

        System.out.println(unsafe.compareAndSwapObject(target, 24, null, 5));
    }
}

class Target {
    int intParam=3;
    long longParam;
    String strParam;
    String strParam2;
}
