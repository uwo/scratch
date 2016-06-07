(ns scratch.debounce
  (:require [clojure.core.async :as a :refer
             [alts! timeout chan >! <! go-loop go]]))


;backwards
;(defn debounce
;  ([out ms] (debounce (chan) out ms))
;  ([in out ms]
;    (go-loop [last-val nil]
;      (let [val (if (nil? last-val) (<! in) last-val)
;            timer (timeout ms)
;            [new-val ch] (alts! [in timer])]
;        (condp = ch
;          timer (do (>! out val) (recur nil))
;          in (recur new-val))))
;    in))


(defn debounce [in ms]
  (let [out (chan)]
    (go-loop [last-val nil]
      (let [val (if (nil? last-val) (<! in) last-val)
            timer (timeout ms)
            [new-val ch] (alts! [in timer])]
        (condp = ch
          timer (do (>! out val) (recur nil))
          in (recur new-val))))
    out))

(defn debounce-fn [f ms]
  "Assumes the debounced function returns a channel, which is piped to the
  return value of the debounced function"
  (let [in (chan)
        out (chan)]
    (go-loop [last-val nil]
      (let [args (if (nil? last-val) (<! in) last-val)
            timer (timeout ms)
            [new-args ch] (alts! [in timer])]
        (condp = ch
          timer (do (a/pipe (apply f args) out false) (recur nil))
          in (recur new-args))))
    (fn [& args]
      (put! in args)
      out)))

(defn debounce-action [f ms]
  (let [in (chan)
        out (chan)]
    (go-loop [last-val nil]
      (let [args (if (nil? last-val) (<! in) last-val)
            timer (timeout ms)
            [new-args ch] (alts! [in timer])]
        (condp = ch
          timer (do (apply f args) (recur nil))
          in (recur new-args))))
    (fn [& args]
      (put! in args)
      out)))
