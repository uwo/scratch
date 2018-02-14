(ns scratch.spec.lab
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.gen.alpha :as gen]))


;; IMPORTANT: s/and conforms as it flows values through predicates,
;; hence you can destructure the results of s/or, s/cat, etc

;; Qs
;; Q: closing over resources, like a db, when defining specs
;; Q: chance of docstrings on s/def's?
;; Q: 'or and 'and less useful than thought? s/keys

;; anonymous functions might not be supported in the future
;; Q:would they also not support named anonymous fns?

;; Q: incremental validations on client forms
;; Q: run all without requires, then with requires
;; Q: validating the 'same' data with different validations requirements (lol diff specs)

(s/valid? string? "abc")

(s/exercise (s/int-in 0 11))

(s/exercise #{\- \1 \2 \3 \4 \5 \6 \7 \8 \9 \/ \X})

(s/valid? inst? (java.util.Date.))

(s/exercise (s/double-in :NaN? false :infinite? false))

;;(s/exercise #(re-matches #"SKU-[0-9]+" %))
(s/valid? #(re-matches #"SKU-[0-9]+" %) "SKU-1231")

;; work around for file-less namespace aliasing 
(create-ns 'my.domain)
(alias 'd 'my.domain)


;; you care about the order of predicates in s/and when using a
;; generator

;; think about the distribution of values generated when using arbitrary
;; predicate functions. you might not ever generate a value that
;; satisfies; you'll need to use a custom generator in these situations


;; return of conform doesn't look like valid? when dealing with
;; branching, like s/or and s/alt

;; lookup: regular expression derivatives

;; s/or appears to short circuit 

;; s/*explain-out*
;; s/explain-printer

(s/def ::local-name (s/and simple-symbol? #(not= '& %)))
(s/exercise ::local-name)

(s/def ::priviledged (s/int-in 1 1025))
(s/def ::unpriviledged (s/int-in 1025 65537))

(s/def ::port
  (s/or :priv ::priviledged
        :unpriv ::unpriviledged))

(s/def ::port-2 (s/and ::port (s/conformer val)))

(s/conform ::port 1024)
(s/conform ::port 3333)

(s/conform ::port-2 1024)
(s/conform ::port-2 3333)

(map (partial s/unform ::port) [(s/conform ::port 1024)
                                (s/conform ::port 3333)])

;; no unform fn for conformer, unless you provide it!!
(map (partial s/unform ::port-2) [(s/conform ::port-2 1024)
                                  (s/conform ::port-2 3333)])

;; providing unform fn to conformer:
(s/def ::port-3
  (s/and ::port (s/conformer
                  val
                  #(if (<= % 1024)
                     [:priv %]
                     [:unpriv %]))))

(s/unform ::port-3 (s/conform ::port-3 1024))


;; use low :gen-max, e.g. 3, for composite collections


;; coverage. `s/registry` - check that all incoming attributes are
;; contained in your registry using `s/get-spec`

;; future work might return keys that were not in the required set

;; s/merge for union s/and for intersection

#_(s/conform (s/coll-of string? :distinct true) ["a" "b"])
#_(s/conform (s/coll-of boolean? :max-count 5) (take 5 (repeat true)))
#_(s/exercise (s/coll-of (s/coll-of int? :gen-max 3) :kind set? :gen-max 3))
#_(s/exercise (s/map-of string? int? :gen-max 3))


;; using recursion to spec a binary tree

(s/def ::node
  (s/or :branch (s/coll-of ::node :max-count 2)
        :leaf int?))

#_(clojure.pprint/pprint
  (s/conform ::node [[1 2] [3 [4 5]]]))

#_(s/exercise ::node)


(s/def :ingredient/name string?)
(s/def :ingredient/quantity (s/and number? pos?))
(s/def :ingredient/unit keyword?)

(s/def ::ingredient (s/keys :opt [:ingredient/name :ingredient/quantity :ingredient/unit]))
(def water #:ingredient{:name "water" :quantity 10 :unit :ounce})
(def butter #:ingredient{:name "butter" :quantity 1/2 :unit :tablespoon})

#_(s/conform ::ingredient water)
#_(s/conform ::ingredient butter)

(def toast
  #:recipe{
    :name "Buttered toast"
    :description "Like bread, but more tasty"
    :ingredients [
      #:ingredient{:name "bread" :quantity 2 :unit :slice}
      #:ingredient{:name "butter" :quantity 1 :unit :teaspoon}]
    :steps ["Toast two slice of bread in the toaster."
            "Spread butter on toast."]
    :servings 1})

(s/def ::recipe/name string?)
(s/def ::recipe/description string?)
(s/def ::recipe/description ::recipe/ingredients)
(s/def ::recipe/ingredients (s/coll-of ::ingredient)
(s/def ::recipe/steps (s/coll-of string?)
(s/def ::recipe/servings int?)
(s/def ::recipe
  (s/keys :req [::recipe/name
                ::recipe/description
                ::recipe/ingredients
                ::recipe/steps
                ::recipe/servings])))

#_(s/conform ::recipe toast)

;; events

(s/def :event/type keyword?)  ;; used to indicate event type
(s/def :event/timestamp int?)

(defmulti event-type :event/type)
(defmethod event-type :event/quote [_]
  (s/keys :req [:event/type :event/timestamp :quote/ticker]))
(s/def :event/event (s/multi-spec event-type :event/type))
(s/def :quote/ticker (s/and string?  #(<= 1 (count %) 4)))

#_(s/conform :event/event
    {:event/type :event/quote
     :event/timestamp 1463970123001
     :quote/ticker "AAPL"})

;; hybrid maps
(s/def :game/rule-set #{:cthulhu})
(s/def :game/dice-count int?)
(s/def :game/opts
  (s/keys :opt [:game/rule-set :game/dice-count]))

;; wrong direction
(s/def ::player-hp (s/map-of string? int?))
(s/def ::hybrid (s/merge :game/opts ::player-hp))
#_(s/conform ::player-hp {"Amy" 200 "Bill" 100})
#_(s/conform :game/opts {:game/rule-set :cthulhu :game/dice-count 5})
;; nope
#_(s/explain ::hybrid
             {"Amy" 200
              "Bill" 100
              :game/rule-set :cthulhu
              :game/dice-count 5})

(s/def :game/tuple-kv (s/tuple string? int?))
#_(s/conform :game/tuple-kv ["Amy" 200])
(s/def :game/tuple-option (s/tuple keyword? any?))
#_(s/conform :game/tuple-option  [:game/rule-set :cthulhu])

(s/def :game/entries
  (s/or :kv (s/coll-of :game/tuple-kv)
        :opt (s/coll-of :game/tuple-option)))

(s/def :game/scores
  (s/merge :game/opts :game/entries))

;; note odd? doesn't have an associated generator because it works for
;; many types you'll need to pair it with a type constraining predicate
;; first e.g. (s/and int? odd?)


;; alternating
(s/def ::pairs-of-str-num (s/* (s/cat :s string? :n number?)))
#_(s/explain ::pairs-of-str-num ["a" 5 "b" 0.2])

;; note approaches for collection of pairs
(s/def ::altks2 (s/coll-of (s/tuple string? number?)))
#_(s/conform ::altks2 [["a" 5] ["b" 0.2]])

;; force new  nested sequential context with `s/spec`
(s/def ::altks3 (s/* (s/spec (s/cat :s string? :n number?))))
#_(s/conform ::altks3 [["a" 5] ["b" 0.2]])


;; regex spec for polygon made of any number of x y coordinates
(s/def ::point (s/cat :x int? :y int?))
(s/def ::polygon (s/* ::point))
(s/def ::polygon-3 (s/& (s/* ::point)
                        #(> (count %) 2)))
#_(s/conform ::polygon [0 0, 5 5, 10 10])
#_(s/conform ::polygon-3 [0 0, 5 5, 10 10])
#_(s/conform ::polygon-3 [0 0, 5 5]) ;;invalid

(s/def ::range
  (s/? (s/cat :start (s/? number?)
              :end number?
              :step (s/? number?))))

#_(s/conform ::range [])
#_(s/conform ::range [10])
#_(s/conform ::range [5 10])
#_(s/conform ::range [0 5/2 1/2])

;; note s/with-gen wants a thunk that returns a generator to avoid
;; calling the generator code


;; biggest benefit of spec comes from speccing functions, which puts a
;; premium on generator writting skills, esp. modeling input relations,
;; etc

;; note weighted generator combinators; `s/nilable` uses this


;; generators
(s/def ::i int?)
#_(s/exercise ::i 10 {::i #(gen/return 42)})
#_(s/exercise ::i 10 {::i #(s/gen #{1 2 3})})

(s/def ::kwid
  (s/with-gen
    (s/and qualified-keyword? #(= (namespace %) "xyz"))
    #(gen/fmap
       (fn [s] (keyword "xyz" s))
       (gen/string-alphanumeric))))

#_(gen/sample (s/gen ::kwid) 5)

(s/def ::kwid2 
  (s/with-gen
    (s/and qualified-keyword?
           #(str/starts-with? (namespace %) "foo")
           #(str/starts-with? (name %) "bar"))
    #(gen/fmap
       (fn [[n1 n2]] (keyword (str "foo" n1) (str "bar" n2)))
       (gen/tuple (gen/string-alphanumeric) (gen/string-alphanumeric)))))

#_(s/valid? ::kwid2 :foo5/bar10)
#_(s/exercise ::kwid2)


;; DESIGN: make a function that touches the database and makes a static
;; generator or spec - not the other way around (generators or specs
;; that query the db). As an example, think about creating a set spec
;; from enums at boot time.


;; function specs

;; using s/get-spec you can retrieve specs, including s/fdef symbols, from the registry.
;; Thena using the Ilookup interface you can grab specs from an fdef instance, e.g. :args
;; This would work if the fdef for clojure.core/subs were available:
#_(-> 'clojure.core/subs s/get-spec :args s/gen)


;; DESIGN: use conform, valid or -maybe- assert for validation at boundaries

(s/fdef clojure.core/range
  :args ::range
  :ret seqable?)
(stest/check 'clojure.core/range)



