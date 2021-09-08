package race.condition;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/8/2021 8:59 PM
 */
public class Counting {
    public static void main(String[] args) throws InterruptedException {
        class Counter {
            private int count = 0;

            // synchronized 来避免加法错误
            public synchronized void increment() {
                ++count;
            }

            public int getCount() {
                return count;
            }
        }

        Counter counter = new Counter();

        class CounterThread extends Thread {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    counter.increment();
                }
            }
        }

        CounterThread thread1 = new CounterThread();
        CounterThread thread2 = new CounterThread();
        long startTime = System.nanoTime();
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
        long endTime = System.nanoTime();
        System.out.println("程序运行时间： " + (endTime - startTime) + "ns");
        System.out.println(counter.getCount());
        // 私有属性也能直接访问
        System.out.println(counter.count);
    }
}
