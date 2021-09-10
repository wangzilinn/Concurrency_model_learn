package mutilock;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/9/2021 9:56 PM
 */
public class ConditionPhilosopher extends Thread {
    final private int id;
    private boolean eating;
    private ConditionPhilosopher leftPhilosopher;
    private ConditionPhilosopher rightPhilosopher;
    final private ReentrantLock table;
    final private Condition condition;

    public ConditionPhilosopher(int id, ReentrantLock table) {
        this.id = id;
        this.eating = false;
        this.table = table;
        this.condition = table.newCondition();
    }

    public void setLeftPhilosopher(ConditionPhilosopher leftPhilosopher) {
        this.leftPhilosopher = leftPhilosopher;
    }

    public void setRightPhilosopher(ConditionPhilosopher rightPhilosopher) {
        this.rightPhilosopher = rightPhilosopher;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            while (true) {
                think();
                eat();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void think() throws InterruptedException {
        // 先锁住桌子，此时只能有一个人操作
        table.lock();
        try{
            eating = false;
            // 放下左边筷子，通知左边
            leftPhilosopher.condition.signal();
            // 放下右边筷子，通知右边
            rightPhilosopher.condition.signal();
        }finally{
            // 解锁桌子
            table.unlock();
        }
        System.out.println(id + " is thinking");
        Thread.sleep(1000);
    }

    private void eat() throws InterruptedException {
        // 先锁住桌子，此时只能由一个人操作
        table.lock();
        try {
            // 如果左边或者右边在吃饭
            while (leftPhilosopher.eating || rightPhilosopher.eating) {
                // 则释放锁，并原地阻塞，如果左边或右边的人发了signal，则恢复执行
                condition.await();
            }
            System.out.println(id + " is eating");
            eating = true;
        } finally {
            table.unlock();
        }
        Thread.sleep(1000);
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock table = new ReentrantLock();
        ConditionPhilosopher philosopher1 = new ConditionPhilosopher(1, table);
        ConditionPhilosopher philosopher2 = new ConditionPhilosopher(2, table);
        ConditionPhilosopher philosopher3 = new ConditionPhilosopher(3, table);

        philosopher1.setLeftPhilosopher(philosopher3);
        philosopher1.setRightPhilosopher(philosopher2);

        philosopher2.setLeftPhilosopher(philosopher1);
        philosopher2.setRightPhilosopher(philosopher3);

        philosopher3.setLeftPhilosopher(philosopher2);
        philosopher3.setRightPhilosopher(philosopher1);

        philosopher1.start();
        philosopher2.start();
        philosopher3.start();

        philosopher1.join();
        philosopher2.join();
        philosopher3.join();
    }
}
