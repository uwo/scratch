(ns scratch.datomic.most-recent-since
  (:require [datomic.api :as d]))

;; ultimately not the direction we want to go

(def rules
  "A collection of rules that, given a bill, returns the transaction
   id(s) when the named event occured. In some cases, like `published?`,
   tx will bind to only one id, and in others, like `cleared-audit?`, tx
   can bind to one or more tx ids.

   Such a tx id can be used to walk back to when a user or system made
   the initial assertion that led the even of interest."

  '[

    ;; a bill is created when :ti.bill/id is first asserted
    [(created? [?b] ?tx)
     [?b :ti.bill/id _ ?tx]]

    ;; a bill is invoiced when associated invoice-package is published
    [(invoiced? [?b] ?tx)
     [?d :ti.invoice-detail/bill ?b]
     [?i :ti.invoice/invoice-details ?d]
     [?i :ti.invoice/invoice-package ?ip]
     [?ip :ti.invoice-package/state :published ?tx] ]

    ;; a bill has cleared audit when its state becomes ready-for-invoicing
    [(cleared-audit? [?b] ?tx)
     [?b :ti.freight-bill/state :ready-for-invoicing ?tx]]

    ;; a bill is user-updated? when a ...
    ;; what's the meaning of updated?
    ;; 1) bill edited on audit screen
    ;; 2) user sent back to audit from invoice (reject needs user)
    ;[(user-updated? [?b] ?tx)
    ; [?tx :ti.source/user]
    ; ]

    ])

  (defn invoiced-tx
    [db bill]
    (d/q '[:find ?tx .
           :in $ % ?b
           :where
           (invoiced? ?b ?tx)]
         db rules bill))

  (defn last-touched
    [db bill]
    (d/q '[:find (max ?t) ?tx
           :in $ ?b
           :where
           [?b _ _ ?tx]
           [?tx :ti.source/user]
           [(datomic.api/tx->t ?tx) ?t]]
         db bill))

  (defn published-by
    [db bill]
    (let [db-at-event (d/as-of db (invoiced-tx db bill))
          [_ tx] (last-touched db-at-event bill)]
      (:ti.source/user (d/entity db tx))))
