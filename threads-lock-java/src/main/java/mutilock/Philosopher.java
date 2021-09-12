package mutilock;

import java.util.Random;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/8/2021 9:27 PM
 */
public class Philosopher extends Thread {
    final private int id;
    final private Chopstick leftHand;
    final private Chopstick rightHand;
    final private Random random;

    public Philosopher(int id, Chopstick left, Chopstick right, boolean order) {
        this.id = id;
        if (!order) {
            // 可能造成死锁的情况：
            this.leftHand = left;
            this.rightHand = right;
        } else {
            // 规定获取资源的顺序，左手总拿更大号的筷子，避免造成死锁
            if (left.getId() > right.getId()) {
                this.leftHand = left;
                this.rightHand = right;
            } else {
                this.leftHand = right;
                this.rightHand = left;
            }
        }
        random = new Random();
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建三个哲学家
        Chopstick chopstick1 = new Chopstick(1);
        Chopstick chopstick2 = new Chopstick(2);
        Chopstick chopstick3 = new Chopstick(3);

        // 当同时拿起左边的筷子时, 线程死锁
        Philosopher philosopher1 = new Philosopher(1, chopstick1, chopstick2, false);
        Philosopher philosopher2 = new Philosopher(2, chopstick2, chopstick3, false);
        Philosopher philosopher3 = new Philosopher(3, chopstick3, chopstick1, false);

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
                // 书中的1000要等好久，100复现率更高
                System.out.println(id + " waiting");
                Thread.sleep(random.nextInt(100));
                synchronized (leftHand) {
                    System.out.println(id + " take " + leftHand.getId());
                    synchronized (rightHand) {
                        System.out.println(id + " take " + rightHand.getId());
                        Thread.sleep(random.nextInt(100));
                        System.out.println(id + " eat");
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


