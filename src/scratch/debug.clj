(ns scratch.debug
  ;(:require '[datomic.api as d])
  )


;Bill to be published:

(def thingy
  {:ti.bill/received #inst "2016-06-12T00:00:00.000-00:00",
   :ti.freight-bill/mode {:db/id 17592186045493},
   :ti.bill/terms {:db/id 17592186045482},
   :ti.freight-bill/customer-total 87.65M,
   :ti.freight-bill/billed-charges {:db/id 17592188100980,
                                    :ti.charge-group/total-weight 1M,
                                    :ti.charge-group/total-charges 87.65M,
                                    :ti.charge-group/total-pieces 1},
   :ti.freight-bill/carrier-remit-to {:db/id 17592186070512},
   :ti.bill/carrier {:db/id 17592186047465,
                     :ti.carrier/id #uuid "5877aecb-9984-48e2-8e59-10f725d0f0d3",
                     :ti.carrier/scac CNWY,
                     :ti.carrier/name XPO LOGISTICS FREIGHT,
                     INC. (LTL),
                     :ti.carrier/locations [{:db/id 17592186070511}],
                     :ti.carrier/pro-format #########,
                     :ti.carrier/bill-value-restrictions
                     [{:db/id 17592186056169,
                       :ti.carrier.bill-value-restriction/attr :ti.freight-bill/mode,
                       :ti.carrier.bill-value-restriction/values
                       [{:db/id 17592186045493}
                        {:db/id 17592186045494}]}
                      {:db/id 17592186062685,
                       :ti.carrier.bill-value-restriction/attr :ti.freight-bill/service-level,
                       :ti.carrier.bill-value-restriction/values [{:db/id 17592186045502} {:db/id 17592186045505}]}]},
   :ti.bill/pickup #inst "2016-06-11T00:00:00.000-00:00",
   :ti.bill/consignee {:db/id 17592186378100},
   :ti.bill/pro 123123124,
   :ti.bill/account {:db/id 17592186147798,
                     :ti.account/number LANC01,
                     :ti.account/name SNYDER`S-LANCE SNACKS NC, LLC,
                     :ti.account/locations
                     [{:db/id 17592186149106} {:db/id 17592186149395} {:db/id 17592186220532} {:db/id 17592186324587} {:db/id 17592186375240} {:db/id 17592186505291} {:db/id 17592186617875} {:db/id 17592186618099} {:db/id 17592186731540} {:db/id 17592186823734} {:db/id 17592186823758} {:db/id 17592186883090} {:db/id 17592186883106} {:db/id 17592186883130} {:db/id 17592186883138} {:db/id 17592186883154} {:db/id 17592186883178}],
                     :ti.account/bol-format #####**},
   :ti.freight-bill/audit-charges {:db/id 17592188101053,
                                   :ti.charge-group/total-charges 106.35M,
                                   :ti.charge-group/fuel-surcharge 20M},
   :ti.freight-bill/benchmark-charges {:db/id 17592188101052,
                                       :ti.charge-group/total-charges 106.35M,
                                       :ti.charge-group/fuel-surcharge 20M},
   :ti.bill/id #uuid "58791fa6-b959-4ed8-be74-eff7283ad001",
   :db/id 17592188100979,
   :ti.freight-bill/state :rated,
   :ti.freight-bill/next-state :ready-for-invoicing,
   :ti.bill/freight-details [{:db/id 17592188100981,
                              :ti.freight-detail/nmfc {:db/id 17592188100982},
                              :ti.freight-detail/handling-units 1,
                              :ti.freight-detail/weight 1M}],
   :ti.bill/shipper {:db/id 17592186823758},
   :ti.freight-bill/service-level {:db/id 17592186045505},
   :ti.bill/direction {:db/id 17592186045477}})


;18:42:58.326 [pool-18-thread-3] INFO  h.invoicing.adapter.harmonium - Bill received by harmonium adapter:


(def thing2
  {:ti.bill/received #inst "2016-06-12T00:00:00.000-00:00",
   :ti.freight-bill/mode {:db/id 17592186045493},
   :ti.bill/terms {:db/id 17592186045482},
   :ti.freight-bill/customer-total 87.65M,
   :ti.freight-bill/billed-charges {:db/id 17592188100980,
                                    :ti.charge-group/total-weight 1M,
                                    :ti.charge-group/total-charges 87.65M,
                                    :ti.charge-group/total-pieces 1},
   :ti.freight-bill/carrier-remit-to {:db/id 17592186070512},
   :ti.bill/carrier {:db/id 17592186047465,
                     :ti.carrier/id #uuid "5877aecb-9984-48e2-8e59-10f725d0f0d3",
                     :ti.carrier/scac CNWY,
                     :ti.carrier/name XPO LOGISTICS FREIGHT,
                     INC. (LTL),
                     :ti.carrier/locations [{:db/id 17592186070511}],
                     :ti.carrier/pro-format #########,
                     :ti.carrier/bill-value-restrictions
                     [{:db/id 17592186056169,
                       :ti.carrier.bill-value-restriction/attr :ti.freight-bill/mode,
                       :ti.carrier.bill-value-restriction/values [{:db/id 17592186045493} {:db/id 17592186045494}]} {:db/id 17592186062685,
                                                                                                                     :ti.carrier.bill-value-restriction/attr :ti.freight-bill/service-level,
                                                                                                                     :ti.carrier.bill-value-restriction/values [{:db/id 17592186045502} {:db/id 17592186045505}]}]},
   :ti.bill/pickup #inst "2016-06-11T00:00:00.000-00:00",
   :ti.bill/consignee {:db/id 17592186378100},
   :ti.bill/pro 123123124,
   :ti.bill/account {:db/id 17592186147798,
                     :ti.account/number LANC01,
                     :ti.account/name SNYDER`S-LANCE SNACKS NC,
                     LLC,
                     :ti.account/locations [{:db/id 17592186149106} {:db/id 17592186149395} {:db/id 17592186220532} {:db/id 17592186324587} {:db/id 17592186375240} {:db/id 17592186505291} {:db/id 17592186617875} {:db/id 17592186618099} {:db/id 17592186731540} {:db/id 17592186823734} {:db/id 17592186823758} {:db/id 17592186883090} {:db/id 17592186883106} {:db/id 17592186883130} {:db/id 17592186883138} {:db/id 17592186883154} {:db/id 17592186883178}],
                     :ti.account/bol-format #####**},
   :ti.freight-bill/audit-charges {:db/id 17592188101053,
                                   :ti.charge-group/total-charges 106.35M,
                                   :ti.charge-group/fuel-surcharge 20M},
   :ti.freight-bill/benchmark-charges {:db/id 17592188101052,
                                       :ti.charge-group/total-charges 106.35M,
                                       :ti.charge-group/fuel-surcharge 20M},
   :ti.bill/id #uuid "58791fa6-b959-4ed8-be74-eff7283ad001",
   :db/id 17592188100979,
   :ti.freight-bill/state :rated,
   :ti.freight-bill/next-state :ready-for-invoicing,
   :ti.bill/freight-details [{:db/id 17592188100981,
                              :ti.freight-detail/nmfc {:db/id 17592188100982},
                              :ti.freight-detail/handling-units 1,
                              :ti.freight-detail/weight 1M}],
   :ti.bill/shipper {:db/id 17592186823758},
   :ti.freight-bill/service-level {:db/id 17592186045505},
   :ti.bill/direction {:db/id 17592186045477}})

;18:42:58.330 [pool-18-thread-6] INFO  h.invoicing.adapter.harmonium - Bill received by harmonium adapter:

(def thingy3
  {:ti.invoice-detail/account
   {:db/id 17592186147798,
    :ti.account/number "LANC01",
    :ti.account/name "SNYDER`S-LANCE SNACKS NC, LLC,"
    :ti.account/locations
    [{:db/id 17592186149106} {:db/id 17592186149395} {:db/id 17592186220532} {:db/id 17592186324587} {:db/id 17592186375240} {:db/id 17592186505291} {:db/id 17592186617875} {:db/id 17592186618099} {:db/id 17592186731540} {:db/id 17592186823734} {:db/id 17592186823758} {:db/id 17592186883090} {:db/id 17592186883106} {:db/id 17592186883130} {:db/id 17592186883138} {:db/id 17592186883154} {:db/id 17592186883178}],
    :ti.account/bol-format "#####**"},
   :ti.invoice-detail/became-invoiceable #inst "2017-01-13T18:42:58.327-00:00",
   :ti.invoice-detail/invoice-detail-charges [{:ti.invoice-detail-charges/id #uuid "58791fb2-9a2f-4d5b-be8f-c986757b7ceb",
                                               :ti.invoice-detail-charges/amount 87.65M,
                                               :ti.invoice-detail-charges/charge-type :ti.invoice-detail-charges.charge-type/accessorial-cost,
                                               :ti.invoice-detail-charges/ti-gl-code }],
   :ti.invoice-detail/invoice-meta-info {:ti.invoice-meta-info/source-id "58791fa6-b959-4ed8-be74-eff7283ad001",
                                         :ti.invoice-meta-info/source :ti.invoice-meta-info.source/harmony,
                                         :ti.invoice-meta-info/source-account-id "LANC01",
                                         :ti.invoice-meta-info/source-account-name "SNYDER`S-LANCE SNACKS NC, LLC",
                                         :ti.invoice-meta-info/received #inst "2016-06-12T00:00:00.000-00:00",
                                         :ti.invoice-meta-info/vendor
                                         {:db/id 17592186047465,
                                          :ti.carrier/id #uuid "5877aecb-9984-48e2-8e59-10f725d0f0d3",
                                          :ti.carrier/scac "CNWY",
                                          :ti.carrier/name "XPO LOGISTICS FREIGHT, INC. (LTL)",
                                          :ti.carrier/locations [{:db/id 17592186070511}],
                                          :ti.carrier/pro-format "#########",
                                          :ti.carrier/bill-value-restrictions
                                          [{:db/id 17592186056169,
                                            :ti.carrier.bill-value-restriction/attr :ti.freight-bill/mode,
                                            :ti.carrier.bill-value-restriction/values
                                            [{:db/id 17592186045493} {:db/id 17592186045494}]} {:db/id 17592186062685,
                                                                                                :ti.carrier.bill-value-restriction/attr :ti.freight-bill/service-level,
                                                                                                :ti.carrier.bill-value-restriction/values [{:db/id 17592186045502} {:db/id 17592186045505}]}]}}})
