/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Email: hanzhonghua1@xiaomi.com
 * Author: 韩忠华
 * Date:2020/4/19 下午11:00
 */
package com.queue;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延迟队列 实现了BlockingQueue
 * 需要实现 Delayed 接口的两个方法 getDelay 实现延迟多久才可以取出来，当该方方法返回的数大于0，阻塞，线程自旋直到返回的时间小于等于0，在poll元素
 * 和 compareTo 元素排序
 * 出队方法调用的是PriorityQueue.poll()
 * @author HanZhonghua
 * @version 1.0
 */
public class DelayQueueTest {

    public static void main(String[] args) throws InterruptedException {
        long now = System.currentTimeMillis();
        Item item1 = new Item("item1", now + 500);
        Item item2 = new Item("item2",now + 1000);
        Item item3 = new Item("item3",now + 1500);

        DelayQueue<Item> delayQueue = new DelayQueue<>();
        delayQueue.put(item1);
        delayQueue.put(item2);
        delayQueue.put(item3);

        for (int i = 0;i < 3; i++) {
            Item take = delayQueue.take();
            System.out.println(take);
        }

    }

    static class Item implements Delayed {

        private long time;
        private String name;

        public Item(String name, long time) {
            this.name = name;
            this.time = time;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(time - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }


        @Override
        public int compareTo(Delayed o) {
            if(this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS)) {
                return -1;
            } else if(this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS)) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public String toString() {
            return this.name + " - " +this.time;
        }
    }
}
