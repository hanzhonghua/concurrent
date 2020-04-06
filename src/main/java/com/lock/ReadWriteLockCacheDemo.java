/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/1/9 下午9:42
 */
package com.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author HanZhonghua
 * @version 1.0
 * 读写锁，应用于读多写少的场景，读读共享，读写互斥
 * 这种锁不是Java独有的，通用的一项技术，这种锁必须满足3个场景
 *  1.多个线程同时读取变量
 *  2.只有一个线程可以写变量
 *  3.如果一个写线程正在写变量，禁止读线程读取变量
 *  4.建单来讲就是：读读共享，读写互斥，写写互斥
 * 之所以性能优于互斥锁，主要就是读写锁支持多线程读.
 * 这里我们使用一个用ReadWriteLock实现的缓存工具类来熟悉下该工具类的用法。
 * 以下Cache<K,V>，K表示缓存的key，V表示缓存的value，我们使用读写锁来保证缓存的数据安全性，ReadWriteLock是一个接口
 *
 * 使用读写锁时有几点需要注意：
 * 1.读写锁中读锁是不支持条件变量的，如果使用readLock.newCondition()会抛 UnsupportedOperationException 异常
 * 2.不支持锁升级，什么叫锁升级呢？
 *  readLock.lock()
 *      writeLock.lock()
 *      writeLock.uplock()
 *  readLock.unlock()
 *  以上代码就表示锁升级，读取数据时发现没有，然后就写入，ReadWriteLock是不支持锁升级的，读锁没有释放，获取写锁，会导致
 *  写锁永久等待，所以在使用ReadWriteLock时，一定要释放读锁后在获取写锁
 *  说白了就是：获取写锁时读锁和写锁都不能被其它线程占用！！
 *
 *  3.支持锁降级，如下代码，锁降级的好处就是支持同时读。说白了就是获取读锁时没有其它线程占用写锁
 *  writeLock.lock()
 *      readLock.lock()
 *      readLock.uplock()
 *  writeLock.unlock()
 * 其有很多实现
 */
public class ReadWriteLockCacheDemo {

    public static void main(String[] args) throws InterruptedException {

        Cache<String, Object> cache = new Cache<>();
        Object a = cache.get("a");
        cache.put("a",1);

    }

    /**
     * 使用HashMap左右容器缓存数据，因为HashMap不是线程安全的，所以需要使用锁来保证写时候线程安全，提供两个方法put和get
     * 使用缓存比较重要的一点就是初始化缓存，一般来说主要有两种方案：
     *  1.全量初始化，容器启动时候从数据库load到内存，这种一般用在数据量不太大的场景
     *  2.懒加载，也就是访问时写入缓存，先访问缓存，缓存没有在访问数据库，然后写入缓存
     * 这里简单写下懒加载场景
     * @param <K>
     * @param <V>
     */
    static class Cache<K,V> {
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

        V get(K key) throws InterruptedException {
            V v;
            readLock.lock();
            Thread.sleep(5000);
            try {
                v =  map.get(key);
            } finally {
                readLock.unlock();
            }
            if (v != null) {
                return v;
            }
            /*writeLock.lock();
            try {
                v = map.get(key);
                if (v == null) {
                    // 查询数据库
                    v = getDb(key);
                    map.put(key, v);
                }
            } finally {
                writeLock.unlock();
            }*/
            return v;
        }

        private <V> V getDb(K key) {

            Object obj = new Random().nextInt(100);
            return (V) obj;
        }
    }
}
