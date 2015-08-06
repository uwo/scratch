(ns scratch.sorting)

(defn ->comparator
  ([sort-dir]
   (if (= :desc sort-dir)
     #(compare %2 %1)
     #(compare %1 %2)))
  ([sort-dir keyfn]
   (if (= :desc sort-dir)
     #(compare (keyfn %2) (keyfn %1))
     #(compare (keyfn %1) (keyfn %2)))))

(defn ->multi-comparator
  [sort-bys]
  (fn [x y]
    (let [[keyfn dir] (first sort-bys)
          seed ((->comparator dir keyfn) x y)]
      (reduce
        (fn [acc [keyfn dir]]
          (if (= acc 0) 
            ((->comparator dir keyfn) x y)
            acc))
        seed
        (rest sort-bys)))))

(defn ->multi-comparator
  [sort-bys]
  (fn [x y]
    (loop [acc 0 
           ordering sort-bys]
      (if-not (= acc 0)
        acc
        (let [[keyfn dir] (first ordering)
              comparator (->comparator dir keyfn)]
          (recur (comparator x y) (rest ordering)))))))

(def compound-sort
  [[:size :asc] [:speed :asc]])

(def col
  [{:size 3 :speed 1 :temp 3}
   {:size 3 :speed 2 :temp 3}
   {:size 1 :speed 7 :temp 3}
   {:size 1 :speed 8 :temp 3}
   {:size 1 :speed 3 :temp 3}
   {:size 2 :speed 7 :temp 3}
   {:size 2 :speed 5 :temp 3}
   {:size 2 :speed 4 :temp 3}])

(defn repetitive-sort
  [collection compound-sort]
  (loop [acc collection ordering compound-sort]
    (if-not (seq ordering)
      acc
      (let [[keyfn dir] (first ordering)
            sorted-col (sort-by keyfn (->comparator dir) acc)]
        (recur sorted-col (rest ordering))))))



#_(clojure.pprint/pprint col)
#_(clojure.pprint/pprint (repetitive-sort col compound-sort))
#_(clojure.pprint/pprint (repetitive-sort col (reverse compound-sort)))
#_(clojure.pprint/pprint (sort (->multi-comparator compound-sort) col))
