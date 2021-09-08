package hello.world;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/8/2021 8:52 PM
 */
public class HelloWorld {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> System.out.println("hello world from another thread"));

        thread.start();

        Thread.sleep(1);
        // 当前线程让出对处理器的占用
        // Thread.yield();

        System.out.println("hello world from main thread");

        thread.join();
    }
}
