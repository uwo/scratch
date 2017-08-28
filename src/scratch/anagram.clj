(ns scratch.anagram)

;; A word x is an anagram of word y if all the letters in x can be
;; rearranged in a different order to form y

;; This is equivalent to, a word x is an anagram of word y if they shares
;; the same set of letters


(defn f
  [words]
  (into #{}
        (keep (fn [w]
                (let [s (into #{} (filter #(= (set w) (set %)) words))]
                  (when (> (count s) 1)
                    s)))
              words)))

(defn f2
  [words]
  (set (map set (filter #(> (count %) 1) (vals (group-by set words))))))

(comment
  (f2 ["meat" "mat" "team" "mate" "eat"])
  (f2 ["veer" "lake" "item" "kale" "mite" "ever"])

  (= (f ["meat" "mat" "team" "mate" "eat"])
     #{#{"meat" "team" "mate"}})

  (= (f ["veer" "lake" "item" "kale" "mite" "ever"])
     #{#{"veer" "ever"} #{"lake" "kale"} #{"mite" "item"}})
  )
