package interrupt;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/9/2021 8:28 PM
 */
public class Interruptable {
    public static void main(String[] args) throws InterruptedException {
        final ReentrantLock l1 = new ReentrantLock();
        final ReentrantLock l2 = new ReentrantLock();

        Thread thread1 = new Thread(() -> {
            try {
                l1.lockInterruptibly();
                Thread.sleep(1000);
                l2.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                l2.lockInterruptibly();
                Thread.sleep(1000);
                l1.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
        // 等待死锁
        Thread.sleep(2000);
        // 打断死锁
        thread1.interrupt();
        thread2.interrupt();

        thread1.join();
        thread2.join();
        System.out.println("get here");
    }
}
