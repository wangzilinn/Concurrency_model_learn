package alien.method;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/8/2021 10:11 PM
 */
public class Downloader extends Thread {
    private InputStream inputStream;
    private OutputStream outputStream;
    private ArrayList<ProgressListener> listeners;

    public Downloader(URL url, String outputFilename) throws IOException {
        inputStream = url.openConnection().getInputStream();
        outputStream = new FileOutputStream(outputFilename);
        listeners = new ArrayList<>();
    }

    public synchronized void addListener(ProgressListener progressListener) {
        listeners.add(progressListener);
    }

    public synchronized void updateProgress(int n) {
        for (ProgressListener progressListener : listeners) {
            progressListener.onProgress(n);
        }
    }

    public void betterUpdateProgress(int n) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
