(ns user
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]))

(eval 
  (let [infix (read-string "(1 + 1)")]
    (list (second infix) (first infix) (last infix)))
  )

(macroexpand '(-> [[1 [2 [1]]]] first second))
