(ns scratch.util)

(defn find-first
  "Return the first item in a sequence that matches the given predicate.

   Like (first (filter)) except will not do extra work due to chunking"
  [pred coll]
  (let [s (seq coll)
        f (first s)
        more (next s)]
    (if (pred f)
      f
      (when more
        (recur pred more)))))

(defn encode-params
  "turn a map of parameters into a urlencoded string"
  [params]
  (letfn [(encode [s] (js/encodeURIComponent (str s)))]
    (s/join "&"
      (mapcat 
        (fn [[k v]]
          (if (sequential? v) 
            (map #(str (encode k) "[]=" (encode %)) v)
            [(str (encode k) "=" (encode v))]))
        params))))
