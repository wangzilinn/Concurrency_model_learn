package interrupt;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/9/2021 8:22 PM
 */
public class Uninterruptible {
    public static void main(String[] args) throws InterruptedException {
        final Object o1 = new Object();
        final Object o2 = new Object();

        Thread thread1 = new Thread(() -> {
            try {
                synchronized (o1) {
                    Thread.sleep(1000);
                    synchronized (o2) {

                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                synchronized (o2) {
                    Thread.sleep(1000);
                    synchronized (o1) {

                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
        // 等待死锁
        Thread.sleep(2000);

        thread1.interrupt();
        thread2.interrupt();

        thread1.join();
        thread2.join();
    }
}
