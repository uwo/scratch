(ns scratch.backoff)



(let [max-wait (* 1000 60 7)
      backoff-rate 1.5
      initial-wait (* 1000 10)]
  (into []
        (comp
          (take-while #(< % max-wait))
          (map #(/ % 1000)))
        (iterate (partial * backoff-rate) initial-wait)))


