(defproject scratch "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-beta1"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/data.csv "0.1.3"]
                 [com.novemberain/monger "3.0.2"]
                 [com.stuartsierra/component "0.3.1"]
                 [clj-http "2.0.1"]
                 [clucy "0.4.0"]
                 [http-kit "2.1.19"]
                 [org.clojure/core.async "0.3.465"]
                 [instaparse "1.4.1"]
                 [com.datomic/clj-client "0.8.606"]
                 [com.datomic/datomic-free
                  "0.9.5561.50"
                  ;"0.9.5530"
                  :exclusions [com.google.guava/guava
                               commons-codec
                               joda-time
                               org.slf4j/slf4j-log4j12
                               org.slf4j/slf4j-nop]]]
  :profiles {:dev {:source-paths ["dev"]}})
