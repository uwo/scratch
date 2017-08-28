(ns scratch.datomic-component
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [app.datomic.schema :as schema]
            [clojure.spec :as s]
            [clojure.string :as str]
            [suspendable.core :as suspendable]
            [clojure.tools.logging :as log])
  (:import (datomic Connection)))


;; missing code

(s/def :datomic/conn (partial instance? Connection))
(s/def :datomic/uri (s/and string?
                           #(str/starts-with? % "datomic:")))

(defrecord Datomic [uri conn]
  component/Lifecycle
  (start [component]
    (let [created? (d/create-database uri)
          conn (d/connect uri)]
      (when created?
        (log/info "Creating a new datomic database:" uri))
      (schema/ensure-schema conn)
      (assoc component :conn conn)))
  (stop [component]
    (when conn (d/release conn))
    (assoc component :conn nil))
  suspendable/Suspendable
  (suspend [component]
    component)
  (resume [component old-component]
    (if (and (= (dissoc component :conn) (dissoc old-component :conn))
             (some? (:conn old-component))
             ;; Try and sync the db, to ensure that we are still connected
             ;; If not, we shut down the component and try to reconnect.
             (try
               (deref (d/sync (:conn old-component)) 500 false)
               (catch Exception e
                 false)))
      (assoc component :conn (:conn old-component))
      (do (component/stop old-component)
          (component/start component)))))

(defn new-datomic [{:keys [uri] :as config}]
  (map->Datomic {:uri uri}))

(s/fdef new-datomic
        :args (s/cat :config (s/keys :req-un [:datomic/uri])))
