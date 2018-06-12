(ns scratch.postinode-tools
  (:require [scratch.util :as util]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as s]))

(defn rd
  [filepath]
  (with-open [rdr (io/reader filepath)]
    (vec (line-seq rdr))))

(def emails (rd "resources/mailboxes"))

(def companies (rd "resources/companies"))

(def fragments
  [#","
   "the "
   "."
   ;" ltd"
   ;" inc"
   [" & " " "]
   [" - " " "]
   ["/" " "]
   ["-" " "]
   ])

(defn replace-fragments
  [fragment c]
  (if (sequential? fragment)
    (let [[match sub] fragment]
      (s/replace c match sub))
    (s/replace c fragment "")))

(def normalizations
  (let [frag-fns (map #(partial replace-fragments %) fragments)]
    (cons
      (map s/lower-case)
      (map map frag-fns))))

(defn segment-match
  [words email]
  (when (seq words)
    (let [prefix (s/join words)
          pattern (re-pattern (str "^" prefix))]
      (if (re-find pattern email)
        email
        (recur (butlast words) email)))))

(defn invoice?
  [email]
  (re-find #"-invoice@insightfusion\.com$" email))

(defn find-potential-matches
  [c]
  (let [words (s/split c #" ")]
    (filterv (partial segment-match words)
             (filter invoice? emails))))

(defn one?
  [c]
  (= (count c) 1))

(defn post-process
  [e]
  (cond
    (one? e) (first e)
    (empty? e) "NOT FOUND"
  :else e))

(defn zipmap-sorted
  [keys vals]
  (into (sorted-map) (map vec (partition 2 (interleave keys vals)))))

(defn find-emails
  []
  (let [xfs (concat
              normalizations
              [(map find-potential-matches)
               (map post-process)])
        emails (into [] (apply comp xfs) companies)]
    (zipmap-sorted companies emails)))

(defn write-pprint-edn
  [out-file data]
  (with-open [w (io/writer out-file)]
    (binding [*out* w]
      (clojure.pprint/write data))))

(comment

  (in-ns 'scratch.postinode-tools)

  (def company "service lighting and electric supplies inc")

  (def email "servicelightingandelectricsupplies-invoice@insightfusion.com")

  (segment-match (s/split company #" ") email)

  (map find-potential-matches (into [] (apply comp normalizations) companies))

  (write-pprint-edn "emails.txt" (find-emails))

  )

