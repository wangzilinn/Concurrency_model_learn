package word.count;

import java.util.HashMap;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 9/10/2021 9:13 PM
 */
public class WordCount {
    private static final HashMap<String, Integer> counts = new HashMap<>();

    public static void main(String[] args) {
        Iterable<Page> pages = new Pages(100, "D:\\Case\\210413_concurrency_models_learn\\threads-lock-java\\src\\main\\java\\word\\count\\en_bbe.xml");
        for (Page page : pages) {
            Iterable<String> words = new Words(page.getText());
            for (String word : words) {
                counts.compute(word, (k, v) -> (v == null) ? 1 : v + 1);
            }
        }
    }
}
