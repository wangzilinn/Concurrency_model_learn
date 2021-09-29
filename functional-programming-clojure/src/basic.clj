(max 3 5)

(+ 1 (* 2 3))

(def meaning-of-life 42)

meaning-of-life

(if (< meaning-of-life 0) "neg" "pos")

(def droids ["ali", "bob", "cc"])

(count droids)

(droids 0)



(defn percentage [x p] (* x (/ p 100.0)))

(percentage 200 10)

(defn find-min [x y] (min x y))

(find-min 2 4)

;Map:
(def me {:name "wang" :age 12 :sex :male})

(:sex me)

(def counts {"apple" 2 "orange" 1})

(get counts "apple" 0)

(assoc counts "banana" 0)

(assoc counts "orange" 2)

(assoc counts "apple" (inc (get counts "apple" 0)))
