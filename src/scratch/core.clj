(ns scratch.core
  (:require [clojure.pprint :refer [pprint]]
  ))

(defn foo
  "I don't do a whole lot."
  [{:keys [id res id-fn]}]
  (prn id res id-fn))

(comment 
  (def preds [even? #(> % 4)])

  (filter #((apply every-pred preds) %) (range 11))

  (into [] (apply comp (map filter preds)) (range 16))

  (defn banana
    [m]
    (keys (filter val m)))

  #_(banana {:a true :b false :c true})
  )


(defn ->comparator
  [sort-dir]
  (if (= :desc sort-dir)
    #(compare %2 %1)
    #(compare %1 %2)))

(defn- sort*
  [[keyfn dir] collection]
  (sort-by keyfn (->comparator dir) collection))

(defn sort-results
  [sort-bys collection]
  (loop [acc collection ordering sort-bys]
    (if-not (seq ordering)
      acc
      (recur (sort* (first ordering) acc) (rest ordering)))))


(defonce r (read-string (slurp "resources/result")))

(comment 
  (map :ti.bill/pickup r)
  (map :ti.freight-bill/pro r)

  (def thing (mapv (fn [x] (select-keys x [:ti.bill/pickup])) r))

  (pprint (sort-by :ti.bill/pickup ))

  )

(def thing
  {:entity {:ti.freight-bill/mode {:db/ident :ti.freight-bill.mode/air, :ti.enum.val/label "AIR"}, :ti.freight-bill/account {:ti.account/name "particularism testicular receivership", :ti.account/number "HXNZ"}, :ti.freight-bill/billed-charges {:ti.charge-group/discount 1, :ti.charge-group/fuel-surcharge 1, :ti.charge-group/total-charges 1, :ti.charge-group/total-pieces 1, :ti.charge-group/total-weight 1}, :ti.freight-bill/ui-state :submitting, :ti.bill/delivery #inst "2015-02-22T00:00:00.000-00:00", :ti.bill/carrier {:ti.carrier/id #uuid "5613e86b-1216-4a9a-9a52-eeb1229e175a", :ti.carrier/name "TEST REEFER CARRIER", :ti.carrier/scac "TES9"}, :ti.bill/consignee {:db/id [:ti.location/id #uuid "560409ea-9928-42fb-a0fe-50edda2b3e1f"]}, :ti.bill/id #uuid "da99e4da-272a-4082-8d3e-5071582a9366", :ti.freight-bill/pro "1111", :ti.freight-bill/state :init, :ti.freight-bill/next-state :modified, :ti.bill/freight-details #{{:ti.freight-detail/class "1", :ti.freight-detail/description "1", :ti.freight-detail/handling-units 1, :ti.freight-detail/nmfc "1", :ti.freight-detail/weight 1}}, :ti.bill/shipper {:db/id [:ti.location/id #uuid "560409ea-9928-42fb-a0fe-50edda2b3e1f"]}, :ti.freight-bill/service-level {:db/ident :ti.freight-bill.service-level/expedited, :ti.enum.val/label "EXPEDITED"}, :ti.bill/direction {:db/ident :ti.bill.direction/inbound, :ti.enum.val/label "I"}}})
