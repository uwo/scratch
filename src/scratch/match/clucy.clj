(ns scratch.match.clucy
  (:require
    [clojure.string :as str]
    [clojure.pprint :refer [pprint] :rename {pprint pp}]
    [clojure.edn :as edn]
    [clucy.core :as clucy]))

(defn readf [f] (edn/read-string (slurp f)))

(def pre-shipped** (readf "/Users/onno/Downloads/training set/pre-shipped-bills.edn"))

(def shipped** (readf "/Users/onno/Downloads/training set/shipped-bill.edn"))

(def pre-shipped (readf "/Users/onno/Downloads/test-data1436/test-pre-shipped-bills.edn"))

(def shipped (readf "/Users/onno/Downloads/test-data1436/test-shipped-bill.edn"))

(defn init [index xs] (apply clucy/add index xs))

(defonce index (clucy/memory-index))

#_(init index pre-shipped)
#_(clucy/add index {:scac "asdc" :pro "1234"} {:scac "asdc" :pro "2345"} {:scac "asda" :pro "1234"})

(def attrs
  #{:pronumblock :consst :consname :terms :scac
    :carriername :consadd1 :conscity :mode
    :shipzip :shipdate :conszip :shipcity :shipmentno
    :weight :shipadd1 :shipname :shipst})

(def query
  "SELECT TOP 3000
   [Loc] ,[ShipDate] ,[ShipmentNo] ,[CarrierName] ,[Mode]
   ,[ProNumBlock] ,[ConsName] ,[ConsAdd1] ,[ConsCity]
   ,[ConsST] ,[ConsZip] ,[SCAC] ,[Terms] ,[Weight]
   ,[SalesOrderNo] ,[CustomerNo] ,[TIMB_ID] ,[Comments]
   ,[FreightBillCreated] ,[TrailerNum] ,[QuotedAmt]
   FROM CustomerBoLs_MADIX
   WHERE ShipDate > GETDATE()-180
   ORDER BY ShipDate DESC")

(defn escape
  "Escape a string for lucene."
  [s]
  (let [chars (java.util.regex.Pattern/quote  "+-&|!(){}[]^\"~*?:\\")
        ch-set (re-pattern (str "([" chars "])"))]
    (str/replace s ch-set "\\\\$1")))

(defn remove-non-alpha-numeric-chars [s] (str/replace s #"[^a-zA-Z0-9]" " "))

(defn clean
  [s]
  (-> s
      (str/replace #"\b(AND|OR|NOT)\b" "")
      remove-non-alpha-numeric-chars))

(defn words [s] (str/split s #"\s+"))

(defn tokenize
  [string]
  (str/trim (apply str (->> string clean words (mapv #(str % "~ ")))))) 

#_(tokenize
   "AVERITT - EXPEDITED SERVICES"
   ;"7883 VILLAGE CENTER N"
   ;"SHERRILLS FORD"
   ;"LTL"
   ;"35072"
   ;"Thu Nov 30 00:00:00 EST 2017"
   )

(defn ->search-string
  [specimen]
  (->> specimen
    (into []
          (comp
            (map (fn [[k v]] {:attr k :value v}))
            (map (fn [{:keys [attr value]}]
                   (str (name attr) ":(" (tokenize value) ")")))))
    (str/join " AND ")))

(defn results
  ([search-string]
   (results search-string {:max-results 100}))
  ([search-string opts]
   (let [{:keys [max-results]} opts]
     (into []
           (comp
             (map (fn [match]
                    {:match match
                     :score (:_score (meta match))}))
             ;(map meta)
             ;(map :_score)
             )
           (clucy/search index search-string max-results)))))

(def shipped*
  {:pronumblock "9743233247",
   :consst "NC",
   :consname "PUBLIX SUPERMARKETS",
   :terms "PPD",
   :scac "AVRT",
   :carriername "AVERITT EXPRESS INC.",
   :consadd1 "7883 VILLAGE CENTER N",
   :conscity "SHERRILLS FORD",
   :mode "LTL",
   :shipzip "35072",
   :shipdate #inst "2017-11-30T00:00:00.000-00:00",
   :conszip "28673",
   :shipcity "GOODWATER",
   :shipmentno "1352725",
   :weight 660M,
   :shipadd1 "1537 S MAIN ST",
   :shipname "MADIX INC",
   :shipst "AL"})

#_(pp
    (let [specimen shipped]
      (-> 
        (->search-string specimen)
        results
        )))


#_(pp (results "pronumblock:9743233247~"))
#_(pp (results (->search-string {:scac "asdf" :pro "1235"})))

(select-keys shipped
             #{:pronumblock
               :consst
               :consname
               :terms
               :scac
               :carriername
               :consadd1
               :conscity
               :mode
               :shipzip
               :shipdate
               :conszip
               :shipcity
               :shipmentno
               :weight
               :shipadd1
               :shipname
               :shipst
               }
             )
