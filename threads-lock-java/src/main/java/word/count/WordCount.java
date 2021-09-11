package word.count;

import java.util.HashMap;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/10/2021 9:13 PM
 */
public class WordCount {
    private static final HashMap<String, Integer> counts = new HashMap<>();

    public static void main(String[] args) {
        Iterable<Page> pages = new Pages(FilePath.babble);
        long start = System.currentTimeMillis();
        for (Page page : pages) {
            Iterable<String> words = new Words(page.getText());
            for (String word : words) {
                counts.compute(word, (k, v) -> (v == null) ? 1 : v + 1);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println(counts);
    }
}
