/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/7/21 下午2:36
 */
package com.threadpool;

/**
 * @author HanZhonghua
 * @version 1.0
 */
public class Demo {

    public static void main(String[] args) {

        int[] arr = {1,2,3,4,5};
        System.out.println(getResult(arr, 0, arr.length-1, 2));
    }

    public static int getResult (int[] arr, int left, int right, int traget) {
        if (arr==null || arr.length==0) {
            return -1;
        }
        while (left <= right) {
            int middle = (left +right) / 2;
            if (arr[middle] == traget) {
                return middle;
            } else if (arr[middle] < traget) {
                left = middle + 1;
            } else if (arr[middle] > traget) {
                right = middle - 1;
            }
        }
        return -1;
    }
}
