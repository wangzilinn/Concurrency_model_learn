package mutilock;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/9/2021 8:56 PM
 */
public class TimeoutPhilosopher extends Thread {
    final private int id;
    final private TimeoutChopstick leftHand;
    final private TimeoutChopstick rightHand;
    final private Random random;

    public TimeoutPhilosopher(int id, TimeoutChopstick leftHand, TimeoutChopstick rightHand) {
        this.id = id;
        this.leftHand = leftHand;
        this.rightHand = rightHand;
        this.random = new Random();
    }

    public static void main(String[] args) throws InterruptedException {
        TimeoutChopstick chopstick1 = new TimeoutChopstick(1);
        TimeoutChopstick chopstick2 = new TimeoutChopstick(2);
        TimeoutChopstick chopstick3 = new TimeoutChopstick(3);

        // 当同时拿起左边的筷子时, 线程死锁
        TimeoutPhilosopher philosopher1 = new TimeoutPhilosopher(1, chopstick1, chopstick2);
        TimeoutPhilosopher philosopher2 = new TimeoutPhilosopher(2, chopstick2, chopstick3);
        TimeoutPhilosopher philosopher3 = new TimeoutPhilosopher(3, chopstick3, chopstick1);

        philosopher1.start();
        philosopher2.start();
        philosopher3.start();

        philosopher1.join();
        philosopher2.join();
        philosopher3.join();
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(id + " waiting");
                Thread.sleep(random.nextInt(10));
                leftHand.lock();
                try {
                    // 没有避免死锁, 而是提供了解决死锁的手段
                    if (rightHand.tryLock(1000, TimeUnit.MILLISECONDS)) {
                        try {
                            // 获取到了右手的筷子
                            System.out.println(id + " eating");
                            Thread.sleep(10);
                        } finally {
                            rightHand.unlock();
                        }
                    } else {
                        // 当没有获取右手的筷子,则继续waiting
                        System.out.println("require timeout");
                    }
                } finally {
                    // 如果获取右手的筷子超时,则直接会执行这里,放弃左边的筷子
                    leftHand.unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
