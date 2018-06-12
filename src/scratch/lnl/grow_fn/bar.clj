(ns scratch.lnl.grow-fn.bar
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]))

(defn g [z])

(defn g'
  [y opts]
  (let [{::keys [z]} opts]
    ;; ...
    ))

(s/def ::z
  ;; here is a docstring for ::z
  any?)

(s/def ::opts
  ;; opts for g'
  (s/keys :req [::z]))

;; now assert or instrument
(s/fdef g'
  :args (s/cat :y any?
               :opts ::opts))

#_(stest/instrument)

;; broken
#_(g' 1 {:test 1})

;; working
#_(g' 1 {::z 1})


(def asdf (partial 

