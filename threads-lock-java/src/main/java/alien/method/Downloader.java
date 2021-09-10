package alien.method;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/8/2021 10:11 PM
 */
public class Downloader extends Thread {
    private InputStream inputStream;
    private OutputStream outputStream;
    private final ArrayList<ProgressListener> listeners;
    private final CopyOnWriteArrayList<ProgressListener> copyOnWriteListeners;

    public Downloader(URL url, String outputFilename) throws IOException {
        inputStream = url.openConnection().getInputStream();
        outputStream = new FileOutputStream(outputFilename);
        listeners = new ArrayList<>();
        copyOnWriteListeners = new CopyOnWriteArrayList<>();
    }

    public Downloader() {
        listeners = new ArrayList<>();
        copyOnWriteListeners = new CopyOnWriteArrayList<>();
    }

    public synchronized void addListener(ProgressListener progressListener) {
        listeners.add(progressListener);
        copyOnWriteListeners.add(progressListener);
    }

    public synchronized void updateProgress(int n) throws InterruptedException {
        // 并发会出现问题，如遍历时其他线程删除了监听器
        for (ProgressListener progressListener : listeners) {
            progressListener.onProgress(n);
        }
        // 使用写时复制的listeners就不用像下面一样每次都复制了
        for (ProgressListener progressListener : copyOnWriteListeners) {
            progressListener.onProgress(n);
        }
    }

    @SuppressWarnings("unchecked")
    public void betterUpdateProgress(int n) throws InterruptedException {
        ArrayList<ProgressListener> progressListenerCopy;
        // 减少持有锁的时间
        synchronized (this) {
            // 此处的clone会把持有的引用数组也复制一份
            progressListenerCopy = (ArrayList<ProgressListener>) listeners.clone();
        }
        for (ProgressListener progressListener : progressListenerCopy) {
            progressListener.onProgress(n);
        }
    }

    @Override
    public void run() {
        int n;
        int total = 0;
        byte[] buffer = new byte[1024];

        try {
            while ((n = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, n);
                total += n;
                updateProgress(total);
            }
            outputStream.flush();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Downloader downloader = new Downloader();
        for (int i = 0; i < 5; ++i) {
            int finalI = i;
            downloader.addListener(current -> {
                Thread.sleep(1000);
                System.out.println("id:" + finalI + "current " + current);
            });
        }

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                try {
                    downloader.betterUpdateProgress(i);
                    // 如果使用该方法会报异常：ConcurrentModificationException
                    // downloader.updateProgress(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            downloader.listeners.remove(0);
        }).start();
    }
}
