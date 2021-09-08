package ooe;

/**
 * OOE：out-of-order execution 乱序执行
 * @Author: wangzilinn@gmail.com
 * @Date: 9/8/2021 9:16 PM
 */
public class Puzzle {
    static boolean answerReady = false;
    static int answer = 0;
    static Thread thread1 = new Thread(() -> {
        // 此处两行没有锁限制,可能乱序执行,先执行answerReady
        answer = 42;
        answerReady = true;
    });

    static Thread thread2 = new Thread(() -> {
        if (answerReady) {
            // Answer在上述乱序执行的情况下,可能为0
            System.out.println("Answer is " + answer);
        } else {
            System.out.println("No answer");
        }
    });

    public static void main(String[] args) throws InterruptedException {
        thread2.start();
        thread1.start();
        thread1.join();
        thread2.join();
    }
}
