;使用递归
(defn recursive-sum [numbers]
  (if (empty? numbers)
    0
    (+ (first numbers) (recursive-sum (rest numbers)))))

;使用reduce方法 三个参数:迭代匿名方法, 初始值, 迭代列表
(defn reduce-sum [numbers]
  (reduce (fn [acc x] (+ acc x)) 0 numbers))

;
(defn simplest-sum [numbers]
  (reduce + numbers))

;并行:
(ns sum
  (:require [clojure.core.reducers :as r]))
(defn parallel-sum [numbers]
  (r/fold + numbers))

(def numbers (into [] (range 0 10000000)))

(defn simplest-sum [numbers]
  (reduce + numbers))

(time (simplest-sum numbers))

(time (parallel-sum numbers))



