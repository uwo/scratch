(require '[datomic.api :as d])

(d/create-database "datomic:mem://counter-example")
;=> true
(def c (d/connect "datomic:mem://counter-example"))
;=> #'user/c

;; The essential features of creating and using an auto-increment counter in datomic:
;;
;; 1. A counter entity must store the current value and a nonce.
;; 2. The current value must be incremented AND A UNIQUE NONCE ADDED whenever
;;    the counter value is retrieved.
;;
;; The nonce is to guarantee that a counter value is never read more than once
;; in a transaction. Since transaction functions only have access to the
;; database before the transaction, they cannot know if other assertions in
;; the same transaction are using and incrementing the counter value.
;; Without a nonce, such transactions would succeed, but the same counter
;; value would be used twice and only incremented once. The nonce ensures
;; that such transactions fail with a datom conflict error.

@(d/transact c [{:db/ident       :counter/name
                 :db/doc         "Unique name for an autoincrement counter entity.
  Must also have :counter/value initialized on the same entity.
  Counter entities should probably use a separate counter partition."
                 :db/valueType   :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/unique      :db.unique/value}
                {:db/ident       :counter/nonce
                 :db/doc         "Used by counter tx functions to ensure a counter is not incremented
  more than once in a tx."
                 :db/valueType   :db.type/uuid
                 :db/cardinality :db.cardinality/one
                 :db/noHistory   true}
                {:db/ident       :counter/value
                 :db/doc         "Current value of the counter. Do not use or assign directly--use :counter.fn/add-counter-value"
                 :db/valueType   :db.type/long
                 :db/cardinality :db.cardinality/one
                 :db/noHistory   true}
                {:db/ident :counter.fn/add-counter-value
                 :db/doc   "Add the current value of a counter to a given entity + attr, and increment the counter.
  A counter's value can only be used once per transaction."
                 :db/fn    (d/function {:lang    :clojure
                                        :params  '[db entity attr counter-name]
                                        :require '[[datomic.api :as d]]
                                        :imports '[[java.util UUID]]
                                        :code    '(let [counter (d/entity db [:counter/name counter-name])
                                                        value   (:counter/value counter)]
                                                    [{:db/id         (:db/id counter)
                                                      :counter/nonce (UUID/randomUUID)
                                                      :counter/value (inc value)}
                                                     [:db/add entity attr value]])})}])

;; Let's add an (optional) partition for counters
@(d/transact c [{:db/id (d/tempid :my.part/counters)
                 :counter/name "foo-counter"
                 :counter/value 0}])

;; And a "unique id" attribute that will use our counter in this example
@(d/transact c [{:db/ident :my/unique-id
                 :db/valueType :db.type/long
                 :db/cardinality :db.cardinality/one}])

;; Example of normal counter use.
@(d/transact c [{:db/id "ent-1"
                 :db/ident :ent-1}
                [:counter.fn/add-counter-value "ent-1" :my/unique-id "foo-counter"]])

;; Note: ent-1 now has a :my/unique-id assigned from the counter.
;; Note: the counter value incremented.
(d/pull-many (d/db c) '[*] [:ent-1 [:counter/name "foo-counter"]])
;=>
;[{:db/id 17592186045424, :db/ident :ent-1, :my/unique-id 0}
; {:db/id 277076930200557,
;  :counter/name "foo-counter",
;  :counter/nonce #uuid"5df6cabb-4884-4bd3-ae7f-5ae1d714dc72",
;  :counter/value 1}]


;; An example of a bad use of a counter. The nonce protects us from this.
;; Unfortunately no better exception info is possible because transaction
;; functions cannot access enough state to detect this problem and throw an
;; exception themselves.

@(d/transact c [[:counter.fn/add-counter-value "ent-2" :my/unique-id "foo-counter"]
                [:counter.fn/add-counter-value "ent-3" :my/unique-id "foo-counter"]])
;IllegalArgumentExceptionInfo :db.error/datoms-conflict Two datoms in the same transaction conflict
;{:d1 [277076930200557 :counter/nonce #uuid "9f7a8c96-859a-4184-b0d1-f100a646f185" 13194139534321 true],
; :d2 [277076930200557 :counter/nonce #uuid "8c20030e-01d9-44b5-a020-c79fa57e38e0" 13194139534321 true]}
;  datomic.error/argd (error.clj:77)