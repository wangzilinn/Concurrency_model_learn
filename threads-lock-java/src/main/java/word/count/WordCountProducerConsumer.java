package word.count;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/11/2021 7:10 PM
 */
public class WordCountProducerConsumer {
    public static void main(String[] args) throws InterruptedException {
        singleConsumer();

        for (int i = 2; i < 16; ++i) {
            multiConsumer(i, false);
        }

        for (int i = 2; i < 16; ++i) {
            multiConsumer(i, true);
        }
    }

    public static void singleConsumer() throws InterruptedException {
        ArrayBlockingQueue<Page> pages = new ArrayBlockingQueue<>(100);
        HashMap<String, Integer> counts = new HashMap<>();
        Thread counter = new Thread(new Counter(pages, counts, false));
        Thread parser = new Thread(new Parser(pages));

        long start = System.currentTimeMillis();
        counter.start();
        parser.start();
        parser.join();
        pages.put(Page.finalPage());
        counter.join();
        long end = System.currentTimeMillis();

        System.out.println(end - start);
        System.out.println(counts);
    }

    public static void multiConsumer(int num_counter, boolean useLocalMap) throws InterruptedException {
        System.out.println("consumer num: " + num_counter);
        ArrayBlockingQueue<Page> pages = new ArrayBlockingQueue<>(100);
        ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newCachedThreadPool();
        long start = System.currentTimeMillis();
        for (int i = 0; i < num_counter; i++) {
            executor.submit(new Counter(pages, counts, useLocalMap));
        }
        Thread parser = new Thread(new Parser(pages));
        parser.start();
        parser.join();
        for (int i = 0; i < num_counter; i++) {
            pages.put(Page.finalPage());
        }
        executor.shutdown();
        executor.awaitTermination(10L, TimeUnit.MINUTES);
        long end = System.currentTimeMillis();

        System.out.println(end - start);
        // System.out.println(counts);
    }

    record Parser(BlockingQueue<Page> queue) implements Runnable {

        @Override
        public void run() {
            try {
                Iterable<Page> pages = new Pages(FilePath.babble);
                for (Page page : pages) {
                    queue.put(page);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    record Counter(BlockingQueue<Page> queue,
                   Map<String, Integer> counts,
                   boolean useLocalMap) implements Runnable {

        @Override
        public void run() {

            Map<String, Integer> countMap;
            if (useLocalMap) {
                countMap = new ConcurrentHashMap<>();
            } else {
                countMap = counts;
            }
            try {
                while (true) {
                    Page page = queue.take();
                    if (page.isFinal()) {
                        if (useLocalMap) {
                            countMap.forEach((k, v) -> counts.merge(k, v, Integer::sum));
                        }
                        break;
                    }
                    Iterable<String> words = new Words(page.getText());
                    for (String word : words) {
                        countMap.compute(word, (k, v) -> (v == null) ? 1 : v + 1);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
