(ns scratch.util.file
  (:require
    [clojure.spec.alpha :as s]
    [clojure.edn :as edn]
    [clojure.data.json :as json]
    [clojure.java.io :as io]))

(defn readf
  "Read resource file"
  [filename]
  (slurp (io/resource filename)))

(defn reade
  "Read edn resource file."
  [filename]
  (edn/read-string {:readers *data-readers*} (readf filename)))

(defn readj
  "Read json resource file." 
  ([filename] (readj filename nil))
  ([filename opts]
   (json/read-str (readf filename)) (select-keys opts [:key-fn :value-fn])))

(s/fdef readj
  :args (s/cat :filename string?
               :opts (s/? (s/keys :opt-un [::key-fn ::value-fn]))))
