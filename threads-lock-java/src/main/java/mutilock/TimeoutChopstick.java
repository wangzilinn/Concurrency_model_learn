package mutilock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/9/2021 8:54 PM
 */
public class TimeoutChopstick extends ReentrantLock{
    final private int id;

    public TimeoutChopstick(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
