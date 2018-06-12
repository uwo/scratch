(ns scratch.lnl.grow-fn.foo
  (:require [clojure.spec.alpha :as s]
            [scratch.lnl.grow-fn.bar :as bar]))

(defn f
  [x y]
  ;; ...
  (bar/g y)
  ;; ...
  )


(defn f'
  ;; our callsite needs to pass information to `bar/g'
  ([x y] (f' x y {})) ;; pass in map with defaults/overrides etc.
  ([x y opts]
   (let [{::bar/keys [z]} opts]
     ;; ...
     ;; alternatively, depending on your needs
     ;; (bar/g' y (select-keys opts [::bar/z ::bar/q]))
     (bar/g' y {::bar/z 'z})
     ;; ...
     )))

(s/def ::opts
  ;; opts passed to f'
  (s/keys :opt [::bar/z]))

#_(f' 1 2 {::bar/z 1 })
