package race.condition;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/8/2021 8:59 PM
 */
public class Counting {
    public static void main(String[] args) throws InterruptedException {
        class Counter {
            private final AtomicInteger atomicCount = new AtomicInteger();
            private int count = 0;
            private int concurrentCount = 0;

            // synchronized 来避免加法错误
            public synchronized void concurrentIncrement() {
                ++concurrentCount;
            }

            public void increment() {
                ++count;
                atomicCount.incrementAndGet();
            }
        }

        Counter counter = new Counter();

        Runnable runnable = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.concurrentIncrement();
                counter.increment();
            }
        };

        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        long startTime = System.nanoTime();
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
        long endTime = System.nanoTime();
        System.out.println("程序运行时间： " + (endTime - startTime) + "ns");
        // 私有属性也能直接访问
        System.out.println(counter.concurrentCount);//2000
        System.out.println(counter.atomicCount);//2000
        System.out.println(counter.count);//1995左右
    }
}
