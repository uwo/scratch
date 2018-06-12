(ns scratch.mindnode
  (:require
    [scratch.datomic.util :as sdu]
    [scratch.util.file :as suf]
    [clojure.java.io :as io]
    [clojure.walk :as walk]
    [datomic.api :as d]))

(defonce conn (sdu/scratch-conn {::sdu/schema-file "schema/mindnode.edn"}))

(def smap
  {:name :mn.node/name
   :children :mn.node/children})

(defn import-json
  [conn filename]
  (let [txdata (->>
                 (suf/readj filename #_{:key-fn identity :value-fn identity})
                 ;(walk/postwalk-replace smap)
                 ;vector
                 )] 
    txdata
    ;@(d/transact conn txdata))
  ))


(comment

  (import-json conn "rpg.json")

  (let [db (d/db conn)]
    #_(d/q '[:find ?e
             :where
             [?e :mn.node/name]]
           db))

  )

