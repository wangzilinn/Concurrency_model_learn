(defn word-frequencies [words]
  (reduce
    (fn [counts word]                                       ;迭代方法
      (assoc                                                ;
        counts                                              ;map
        word                                                ;当前的word
        (inc (get counts word 0))))                         ;
    {}                                                      ;初始值
    words))                                                 ;被迭代的列表


(word-frequencies ["one" "potato" "two" "potato" "three" "potato" "four"])

(frequencies ["one" "potato" "two" "potato" "three" "potato" "four"])
