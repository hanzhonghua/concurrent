/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/1/9 下午9:42
 */
package com.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author HanZhonghua
 * @version 1.0
 * 读写锁，应用于读多写少的场景，读读共享，读写互斥
 * 这种锁不是Java独有的，通用的一项技术，这种锁必须满足3个场景
 *  1.多个线程同时读取变量
 *  2.只有一个线程可以写变量
 *  3.如果一个写线程正在写变量，禁止读线程读取变量
 * 之所以性能优于互斥锁，主要就是读写锁支持多线程读.
 * 这里我们使用一个用ReadWriteLock实现的缓存工具类来熟悉下该工具类的用法。
 * 以下Cache<K,V>，K表示缓存的key，V表示缓存的value，我们使用读写锁来保证缓存的数据安全性，ReadWriteLock是一个接口
 * 其有很多实现
 */
public class ReadWriteLockCacheDemo {

    public static void main(String[] args) {

        System.out.println(1<<16);

    }

    /**
     * 使用HashMap左右容器缓存数据，因为HashMap不是线程安全的，所以需要使用锁来保证写时候线程安全，提供两个方法put和get
     * @param <K>
     * @param <V>
     */
    class Cache<K,V> {
        Map<K, V> map = new HashMap<K, V>();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

        V put(K key, V value) {
            writeLock.lock();
            try {
                return map.put(key, value);
            } finally {
                writeLock.unlock();
            }
        }

        V get(K key) {
            readLock.lock();
            try {
                return map.get(key);
            } finally {
                readLock.unlock();
            }
        }
    }
}
