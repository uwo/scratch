(ns scratch.datomic.util
  (:require
    [clojure.spec.alpha :as s]
    [datomic.api :as d]
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [scratch.util.file :as suf]))

(defn prime-conn!
  [conn schema-file]
  (let [schema (suf/reade schema-file)]
    @(d/transact conn schema)
    conn))

(defn scratch-conn
  "Returns the created scratch datomic:mem connection."
  ([] (scratch-conn {::prime? true}))
  ([opts]
   (let [{::keys [prime? schema-file]
          :or {prime? true}} opts
         uri (str "datomic:mem://" (d/squuid))
         _ (d/delete-database uri)
         _ (d/create-database uri)
         conn (d/connect uri)]
     (if prime? (prime-conn! conn schema-file) conn))))

(s/fdef scratch-conn
  :args (s/cat :opts (s/keys :opt [::prime? ::schema-file])))

