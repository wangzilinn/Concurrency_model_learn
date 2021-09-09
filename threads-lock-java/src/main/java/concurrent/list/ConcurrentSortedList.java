package concurrent.list;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/9/2021 9:19 PM
 */
public class ConcurrentSortedList {
    private class Node{
        int value;
        Node prev;
        Node next;
        ReentrantLock lock = new ReentrantLock();

        public Node() {
        }

        public Node(int value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }

    final private Node head;
    final private Node tail;


    public ConcurrentSortedList() {
        head = new Node();
        tail = new Node();
        head.next = tail;
        tail.prev = head;
    }

    public void insert(int value) {
        Node current = head;
        current.lock.lock();
        Node next = head.next;

        try {
            while (true) {
                // 锁住下一个
                next.lock.lock();
                // 往中间加节点
                try {
                    // 从大到小排序
                    if (value > next.value || next == tail) {
                        Node node = new Node(value, current, next);
                        next.prev = node;
                        current.next = node;
                        return;
                    }
                }finally {
                    current.lock.unlock();
                }
                current = next;
                next = current.next;
            }
        }finally {
            next.lock.unlock();
        }
    }

    @Override
    public String toString() {
        Node current = tail;
        StringBuilder stringBuilder = new StringBuilder();
        // 从后往前输出,实现从小到大
        // 之所以从后往前遍历, 是为了防止同时调用insert和tostring时每次都产生冲突
        while (current != head) {
            ReentrantLock lock = current.lock;
            lock.lock();
            try {
                stringBuilder.append(current.value).append(",");
                current = current.prev;
            }finally {
                lock.unlock();
            }
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        ConcurrentSortedList concurrentSortedList = new ConcurrentSortedList();
        Runnable runnable = () -> {
            Random random = new Random();
            for (int i = 0; i < 50; i++) {
                concurrentSortedList.insert(random.nextInt(100));
            }
        };

        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println(concurrentSortedList);

    }
}
