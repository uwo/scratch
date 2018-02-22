(ns scratch.util/byte-count)

;; https://twitter.com/RobStuttaford/status/966542974271262720

(defn pretty-byte-count
  ([byte-count] (pretty-byte-count byte-count 0))
  ([byte-count step]
   (if (< byte-count 1024)
     (str byte-count (nth ["b" "Kb" "Mb" "Gb" "Tb" "Pb"] step))
     (recur (floor (float (/ byte-count 1024))) (inc step)))))
